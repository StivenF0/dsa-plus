package com.dsastream.util;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// Implementação da codificação de Huffman para compressão sem perdas.
//
// Utiliza um MinHeap para construir a árvore ótima com base na frequência
// de cada caractere no texto original. Caracteres mais frequentes recebem
// códigos binários mais curtos, reduzindo o tamanho total da mensagem.
//
// Fluxo: frequências → MinHeap → árvore de Huffman → tabela de códigos → compressão
public class HuffmanCoding {

    private HuffmanCoding() {}

    // Nó da árvore de Huffman. Nós folha armazenam um caractere e sua frequência;
    // nós internos armazenam apenas a soma das frequências dos filhos
    public static class HuffmanNode implements Comparable<HuffmanNode> {
        char character;
        int frequency;
        HuffmanNode left;
        HuffmanNode right;

        // Construtor para nó folha (caractere real)
        public HuffmanNode(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        // Construtor para nó interno (combinação de dois filhos)
        public HuffmanNode(int frequency, HuffmanNode left, HuffmanNode right) {
            this.character = '\0';
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        // Comparação por frequência para ordenação no MinHeap
        public int compareTo(HuffmanNode other) {
            return Integer.compare(this.frequency, other.frequency);
        }
    }

    // Comprime o texto: retorna uma string binária com os códigos Huffman
    public static String compress(String text) {
        if (text == null || text.isEmpty()) return "";

        Map<Character, Integer> freqMap = buildFreqMap(text);
        HuffmanNode root = buildTree(freqMap);
        if (root == null) return "";

        Map<Character, String> codeMap = new HashMap<>();
        buildCodeMap(root, "", codeMap);

        // Substitui cada caractere pelo seu código binário
        StringBuilder bits = new StringBuilder();
        for (char c : text.toCharArray()) {
            bits.append(codeMap.get(c));
        }
        return bits.toString();
    }

    // Descomprime a string binária de volta ao texto original
    // Precisa do texto original para reconstruir a árvore de Huffman
    public static String decompress(String bits, String originalText) {
        if (bits == null || bits.isEmpty()) return "";

        // Reconstrói a mesma árvore usada na compressão
        Map<Character, Integer> freqMap = buildFreqMap(originalText);
        HuffmanNode root = buildTree(freqMap);
        if (root == null) return "";

        StringBuilder result = new StringBuilder();
        HuffmanNode current = root;

        // Percorre os bits seguindo a árvore: 0 = esquerda, 1 = direita
        // Quando chega a uma folha, encontra o caractere e reinicia da raiz
        for (int i = 0; i < bits.length(); i++) {
            current = (bits.charAt(i) == '0') ? current.left : current.right;

            if (current.isLeaf()) {
                result.append(current.character);
                current = root;
            }
        }
        return result.toString();
    }

    // Retorna estatísticas da compressão: tamanho original, comprimido e taxa
    public static String compressionStats(String text) {
        if (text == null || text.isEmpty()) {
            return "Nenhuma mensagem para comprimir.";
        }

        String compressed = compress(text);
        int originalBytes = text.getBytes(StandardCharsets.UTF_8).length;
        int compressedBits = compressed.length();
        int compressedBytes = (int) Math.ceil(compressedBits / 8.0);
        double rate = (1 - (double) compressedBytes / originalBytes) * 100;

        return String.format(
            "Original: %d bytes | Comprimido: %d bits (%d bytes) | Taxa: %.1f%%",
            originalBytes, compressedBits, compressedBytes, rate
        );
    }

    // Conta a frequência de cada caractere no texto
    private static Map<Character, Integer> buildFreqMap(String text) {
        Map<Character, Integer> freq = new HashMap<>();
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (freq.containsKey(c)) {
                freq.put(c, freq.get(c) + 1);
            } else {
                freq.put(c, 1);
            }
        }
        return freq;
    }

    // Constrói a árvore de Huffman usando um MinHeap.
    // Remove os dois menores, combina, reinsere, até restar apenas a raiz
    private static HuffmanNode buildTree(Map<Character, Integer> freqMap) {
        if (freqMap.isEmpty()) return null;

        MinHeap<HuffmanNode> heap = new MinHeap<>(freqMap.size());

        // Insere cada caractere como um nó folha no heap
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            heap.insert(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        // Combina os dois menores repetidamente até restar um nó (a raiz)
        while (heap.size() > 1) {
            HuffmanNode x = heap.removeMin();
            HuffmanNode y = heap.removeMin();
            HuffmanNode z = new HuffmanNode(x.frequency + y.frequency, x, y);
            heap.insert(z);
        }

        return heap.removeMin();
    }

    // Percorre a árvore da raiz às folhas gerando os códigos binários.
    // Cada caminho: 0 para esquerda, 1 para direita
    private static void buildCodeMap(HuffmanNode node, String prefix, Map<Character, String> codeMap) {
        if (node == null) return;

        if (node.isLeaf()) {
            codeMap.put(node.character, prefix.isEmpty() ? "0" : prefix);
            return;
        }

        buildCodeMap(node.left, prefix + "0", codeMap);
        buildCodeMap(node.right, prefix + "1", codeMap);
    }
}
