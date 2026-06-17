package com.dsaplus.channel;

import com.dsaplus.util.HashMap;
import com.dsaplus.util.MinHeap;

import java.nio.charset.StandardCharsets;

/**
 * Implementação do algoritmo de Huffman para compressão sem perdas.
 * <p>
 * Atribui códigos binários de tamanho variável aos caracteres: os mais frequentes
 * recebem códigos curtos, os menos frequentes recebem códigos longos.
 * A árvore é construída uma única vez a partir de um corpus (conjunto de
 * mensagens representativas) e reutilizada para todas as transmissões.
 */
public class HuffmanCoding {

    /**
     * Nó da árvore binária de Huffman.
     * Folhas armazenam um caractere e sua frequência; nós internos armazenam
     * a soma das frequências dos filhos e não possuem caractere próprio ('\0').
     */
    public static class HuffmanNode implements Comparable<HuffmanNode> {
        char character;
        int frequency;
        HuffmanNode left;
        HuffmanNode right;

        /**
         * Construtor para nó folha (caractere + frequência).
         */
        public HuffmanNode(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        /**
         * Construtor para nó interno (soma de frequências + filhos).
         */
        public HuffmanNode(int frequency, HuffmanNode left, HuffmanNode right) {
            this.character = '\0';
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        /**
         * Comparação por frequência para uso no MinHeap (menor frequência = maior prioridade).
         */
        public int compareTo(HuffmanNode other) {
            return Integer.compare(this.frequency, other.frequency);
        }
    }

    /**
     * Mapeamento caractere → código binário (ex.: 'A' → "011").
     * Construído uma única vez a partir da árvore de Huffman.
     */
    private final HashMap<Character, String> codeMap;

    /**
     * Raiz da árvore de Huffman. Usada na descompressão: percorre-se a árvore
     * bit a bit até encontrar uma folha, que revela o caractere original.
     */
    private final HuffmanNode root;

    /**
     * @param corpus texto representativo das mensagens que serão trafegadas.
     *               A árvore é construída com base na frequência dos caracteres
     *               deste corpus e depois reutilizada para comprimir/descomprimir
     *               qualquer mensagem.
     */
    public HuffmanCoding(String corpus) {
        HashMap<Character, Integer> freqMap = buildFreqMap(corpus);
        this.root = buildTree(freqMap);
        this.codeMap = new HashMap<>();
        if (this.root != null) {
            buildCodeMap(this.root, "", this.codeMap);
        }
    }

    /**
     * Comprime um texto substituindo cada caractere pelo seu código Huffman.
     * Caracteres não presentes no codeMap são silenciosamente ignorados
     * (por isso o corpus deve incluir todos os caracteres esperados).
     *
     * @param text texto original
     * @return string binária (ex.: "01001101")
     */
    public String compress(String text) {
        if (text == null || text.isEmpty()) return "";
        if (codeMap.isEmpty()) return "";

        StringBuilder bits = new StringBuilder();
        for (char c : text.toCharArray()) {
            String code = codeMap.get(c);
            if (code != null) {
                bits.append(code);
            }
        }
        return bits.toString();
    }

    /**
     * Descomprime uma string binária percorrendo a árvore de Huffman.
     * '0' desvia para a esquerda, '1' para a direita. Ao atingir uma folha,
     * o caractere é recuperado e a busca reinicia da raiz.
     *
     * @param bits string binária produzida por compress()
     * @return texto original
     */
    public String decompress(String bits) {
        if (bits == null || bits.isEmpty()) return "";
        if (root == null) return "";

        StringBuilder result = new StringBuilder();
        HuffmanNode current = root;

        for (int i = 0; i < bits.length(); i++) {
            current = (bits.charAt(i) == '0') ? current.left : current.right;
            if (current == null) break;

            if (current.isLeaf()) {
                result.append(current.character);
                current = root;
            }
        }
        return result.toString();
    }

    /**
     * Calcula estatísticas de compressão para uma mensagem.
     */
    public CompressionResult compressionStats(String text) {
        String compressed = compress(text);
        int originalBytes = text.getBytes(StandardCharsets.UTF_8).length;
        int compressedBits = compressed.length();
        int compressedBytes = (int) Math.ceil(compressedBits / 8.0);
        return new CompressionResult(originalBytes, compressedBits, compressedBytes);
    }

    /**
     * Armazena as métricas de uma compressão: tamanho original, bits comprimidos
     * e bytes equivalentes após compactação.
     */
    public static class CompressionResult {
        public final int originalBytes;
        public final int compressedBits;
        public final int compressedBytes;

        public CompressionResult(int originalBytes, int compressedBits, int compressedBytes) {
            this.originalBytes = originalBytes;
            this.compressedBits = compressedBits;
            this.compressedBytes = compressedBytes;
        }

        /**
         * Taxa de redução: (1 - comprimido / original) * 100.
         * Ex.: 35% significa que o dado comprimido ocupa 65% do original.
         */
        public double ratio() {
            return (1 - (double) compressedBytes / originalBytes) * 100;
        }

        public String format() {
            return String.format(
                "Original: %d bytes | Comprimido: %d bits (%d bytes) | Taxa: %.1f%%",
                originalBytes, compressedBits, compressedBytes, ratio()
            );
        }
    }

    // --- Etapas de construção ---

    /**
     * 1ª etapa: conta a frequência de cada caractere no corpus.
     */
    private static HashMap<Character, Integer> buildFreqMap(String text) {
        HashMap<Character, Integer> freq = new HashMap<>();
        for (char c : text.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        return freq;
    }

    /**
     * 2ª etapa: constrói a árvore de Huffman usando um MinHeap.
     * <p>
     * Algoritmo:
     * <ol>
     *   <li>Insere todos os caracteres como nós folha no heap (ordenados por frequência)</li>
     *   <li>Remove os dois nós de menor frequência</li>
     *   <li>Cria um nó interno com a soma das frequências como peso e os dois nós como filhos</li>
     *   <li>Reinsere o nó interno no heap</li>
     *   <li>Repete até restar apenas um nó (a raiz da árvore)</li>
     * </ol>
     * O resultado é uma árvore binária onde caracteres mais frequentes ficam
     * mais próximos da raiz (códigos mais curtos).
     */
    private static HuffmanNode buildTree(HashMap<Character, Integer> freqMap) {
        if (freqMap.isEmpty()) return null;

        MinHeap<HuffmanNode> heap = new MinHeap<>(freqMap.size());

        for (HashMap.Entry<Character, Integer> entry : freqMap.entrySet()) {
            heap.insert(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (heap.size() > 1) {
            HuffmanNode x = heap.removeMin();
            HuffmanNode y = heap.removeMin();
            HuffmanNode z = new HuffmanNode(x.frequency + y.frequency, x, y);
            heap.insert(z);
        }

        return heap.removeMin();
    }

    /**
     * 3ª etapa: percorre a árvore em profundidade (DFS) montando o mapa
     * caractere → código binário. Cada desvio à esquerda adiciona '0' ao
     * prefixo; cada desvio à direita adiciona '1'.
     */
    private static void buildCodeMap(HuffmanNode node, String prefix, HashMap<Character, String> codeMap) {
        if (node == null) return;

        if (node.isLeaf()) {
            codeMap.put(node.character, prefix.isEmpty() ? "0" : prefix);
            return;
        }

        buildCodeMap(node.left, prefix + "0", codeMap);
        buildCodeMap(node.right, prefix + "1", codeMap);
    }
}
