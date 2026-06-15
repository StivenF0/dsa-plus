package com.dsaplus.channel;

import com.dsaplus.server.Server;
import com.dsaplus.util.Logger;
import java.util.ArrayList;
import java.util.List;

public class CommunicationChannel {

    private final HuffmanCoding huffman;
    private final List<Transmission> history;

    public CommunicationChannel(String corpus) {
        this.huffman = new HuffmanCoding(corpus);
        this.history = new ArrayList<>();
    }

    public String compress(String data) {
        String bits = huffman.compress(data);
        HuffmanCoding.CompressionResult stats = huffman.compressionStats(data);
        history.add(new Transmission(data, stats));
        Logger.debug(
            "Channel",
            "Comprimido: \"" +
                data +
                "\" → " +
                stats.compressedBits +
                " bits (" +
                stats.compressedBytes +
                " bytes) | Taxa: " +
                String.format("%.1f%%", stats.ratio())
        );
        return bits;
    }

    public String decompress(String bits) {
        return huffman.decompress(bits);
    }

    public String requestMovie(String compressedReq, Server server, boolean useIndex) {
        Logger.debug("Channel", "Repassando requisição comprimida ao servidor...");
        String compressedRes = server.processMovieRequest(compressedReq, this, useIndex);
        Logger.debug("Channel", "Resposta comprimida recebida do servidor, retornando ao cliente...");
        return compressedRes;
    }

    public void clearHistory() {
        history.clear();
    }

    public List<Transmission> getHistory() {
        return new ArrayList<>(history);
    }

    public String getSummary() {
        if (history.isEmpty()) return "Nenhuma transmissão registrada.";

        int totalOriginal = 0;
        int totalCompressedBits = 0;
        for (Transmission t : history) {
            totalOriginal += t.stats.originalBytes;
            totalCompressedBits += t.stats.compressedBits;
        }
        int totalCompressedBytes = (int) Math.ceil(totalCompressedBits / 8.0);
        double avgRatio = (1 - (double) totalCompressedBytes / totalOriginal) * 100;

        return String.format(
            "Total: %d mensagens | %d bytes → %d bits (%d bytes) | Taxa média: %.1f%%",
            history.size(),
            totalOriginal,
            totalCompressedBits,
            totalCompressedBytes,
            avgRatio
        );
    }

    public static class Transmission {

        public final String message;
        public final HuffmanCoding.CompressionResult stats;

        public Transmission(String message, HuffmanCoding.CompressionResult stats) {
            this.message = message;
            this.stats = stats;
        }
    }
}
