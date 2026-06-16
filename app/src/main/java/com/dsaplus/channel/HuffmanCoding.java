package com.dsaplus.channel;

import com.dsaplus.util.HashMap;
import com.dsaplus.util.MinHeap;

import java.nio.charset.StandardCharsets;

public class HuffmanCoding {

    public static class HuffmanNode implements Comparable<HuffmanNode> {
        char character;
        int frequency;
        HuffmanNode left;
        HuffmanNode right;

        public HuffmanNode(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        public HuffmanNode(int frequency, HuffmanNode left, HuffmanNode right) {
            this.character = '\0';
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        public int compareTo(HuffmanNode other) {
            return Integer.compare(this.frequency, other.frequency);
        }
    }

    private final HashMap<Character, String> codeMap;
    private final HuffmanNode root;

    public HuffmanCoding(String corpus) {
        HashMap<Character, Integer> freqMap = buildFreqMap(corpus);
        this.root = buildTree(freqMap);
        this.codeMap = new HashMap<>();
        if (this.root != null) {
            buildCodeMap(this.root, "", this.codeMap);
        }
    }

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

    public CompressionResult compressionStats(String text) {
        String compressed = compress(text);
        int originalBytes = text.getBytes(StandardCharsets.UTF_8).length;
        int compressedBits = compressed.length();
        int compressedBytes = (int) Math.ceil(compressedBits / 8.0);
        return new CompressionResult(originalBytes, compressedBits, compressedBytes);
    }

    public static class CompressionResult {
        public final int originalBytes;
        public final int compressedBits;
        public final int compressedBytes;

        public CompressionResult(int originalBytes, int compressedBits, int compressedBytes) {
            this.originalBytes = originalBytes;
            this.compressedBits = compressedBits;
            this.compressedBytes = compressedBytes;
        }

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

    private static HashMap<Character, Integer> buildFreqMap(String text) {
        HashMap<Character, Integer> freq = new HashMap<>();
        for (char c : text.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        return freq;
    }

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
