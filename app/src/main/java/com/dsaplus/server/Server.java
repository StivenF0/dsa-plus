package com.dsaplus.server;

import com.dsaplus.model.Movie;
import com.dsaplus.server.ds.ListNode;
import com.dsaplus.server.ds.HashTable;
import com.dsaplus.server.ds.LinkedList;
import com.dsaplus.channel.CommunicationChannel;
import com.dsaplus.common.ds.SplayNode;
import com.dsaplus.common.ds.SplayTree;
import com.dsaplus.util.CsvParser;
import com.dsaplus.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class Server {
    private final LinkedList database;
    private final HashTable index;
    private final SplayTree popularity;

    // 509 foi escolhido como o tamanho da tabela hash por ser o número primo mais próximo de 2^9 (512)
    private static final int TABLE_SIZE = 509;

    public Server() {
        this.database = new LinkedList();
        this.index = new HashTable(TABLE_SIZE);
        this.popularity = new SplayTree();
    }

    public void loadInitialData() {
        Logger.info("Server", "Carregando catálogo de filmes via CSV...");

        List<String[]> records = CsvParser.parseResource("/csv/movies_dataset.csv");

        if (records.isEmpty()) {
            Logger.error("Server", "Nenhum registro encontrado no CSV!");
            return;
        }

        List<Movie> allMovies = new ArrayList<>();

        for (String[] data : records) {
            if (data.length < 3) continue;

            int id = Integer.parseInt(data[0].trim());
            String title = data[1].trim();
            String category = data[2].trim();
            int year = data.length >= 4 && !data[3].trim().isEmpty()
                    ? Integer.parseInt(data[3].trim()) : 0;
            String synopsis = data.length >= 5 ? data[4].trim() : "";

            Movie movie = new Movie(id, title, category, year, synopsis);
            allMovies.add(movie);

            ListNode insertedNode = database.add(movie);
            index.put(id, insertedNode);
        }

        // Pré-popula a árvore splay de popularidade em ordem reversa (menos popular primeiro)
        // para que os filmes mais populares (topo do CSV) fiquem mais próximos da raiz
        for (int i = allMovies.size() - 1; i >= 0; i--) {
            Movie movie = allMovies.get(i);
            popularity.insert(movie.getId(), movie);
        }
        allMovies.clear();

        Logger.info("Server", "Catálogo carregado com sucesso! Total: " + records.size() + " filmes.");
    }

    public Movie requestMovieWithoutIndex(int id) {
        Logger.debug("Server", "Requisição SEM índice ID " + id);
        Movie movie = database.searchSequential(id);
        if (movie != null) {
            popularity.insert(id, movie);
        }
        return movie;
    }

    public Movie requestMovieWithIndex(int id) {
        Logger.debug("Server", "Requisição COM índice ID " + id);
        ListNode foundListNode = index.searchIndexed(id);

        if (foundListNode != null) {
            Movie movie = foundListNode.getMovie();
            popularity.insert(id, movie);
            return movie;
        }
        return null;
    }

    // Processa uma requisição comprimida recebida pelo canal de comunicação
    public String processMovieRequest(String compressedReq, CommunicationChannel channel, boolean useIndex) {
        String reqStr = channel.decompress(compressedReq);
        int id = Integer.parseInt(reqStr);
        Logger.debug("Server", "Descomprimido: ID " + id + (useIndex ? " (COM índice)" : " (SEM índice)"));
        Movie movie = useIndex ? requestMovieWithIndex(id) : requestMovieWithoutIndex(id);
        String payload = movie != null ? movie.toDataString() : "NULL";
        String compressedRes = channel.compress(payload);
        Logger.debug("Server", "Resposta comprimida (" + payload.length() + " bytes → " + compressedRes.length() + " bits)");
        return compressedRes;
    }

    // Retorna os n filmes mais populares (mais próximos da raiz da splay)
    public List<Movie> getTopMovies(int n) {
        List<Movie> top = new ArrayList<>();
        for (SplayNode node : popularity.getTop(n)) {
            top.add(node.getValue());
        }
        return top;
    }

    public SplayTree getPopularity() {
        return popularity;
    }

    // --- Getters e Setters ---

    public LinkedList getDatabase() {
        return database;
    }
}
