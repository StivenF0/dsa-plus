package com.dsaplus.client;

import com.dsaplus.channel.CommunicationChannel;
import com.dsaplus.client.ds.LRUCache;
import com.dsaplus.common.ds.SplayTree;
import com.dsaplus.model.Movie;
import com.dsaplus.server.Server;
import com.dsaplus.util.Logger;

import java.util.List;

public class Client {
    private final LRUCache cache;
    private final SplayTree preferences;
    private final String name;

    @SuppressWarnings("FieldCanBeLocal")
    private final int MAX_CACHE_SIZE = 50;

    public Client(String name) {
        this.name = name;
        this.cache = new LRUCache(MAX_CACHE_SIZE);
        this.preferences = new SplayTree();
    }

    public void viewMovie(Movie movie) {
        if (movie == null) {
            Logger.error(name, "Filme não encontrado no servidor.");
            return;
        }

        Logger.info("Client", name + " assistiu: [ID: " + movie.getId() + "] " + movie.getTitle() + " (" + movie.getCategory() + ", " + movie.getYear() + ")");
        cache.put(movie.getId(), movie);
        preferences.insert(movie.getId(), movie);
    }

    // --- Comunicação com o servidor via canal ---

    public void requestMovie(Server server, CommunicationChannel channel, int searchId, boolean useIndex) {
        Movie movie = cache.get(searchId);

        if (movie == null) {
            String compressedReq = channel.compress(String.valueOf(searchId));
            String compressedRes = channel.requestMovie(compressedReq, server, useIndex);
            String data = channel.decompress(compressedRes);
            movie = "NULL".equals(data) ? null : Movie.fromDataString(data);
        }

        viewMovie(movie);
    }

    // --- Recomendação baseada na árvore splay de preferências ---
    // A raiz da árvore contém o filme mais acessado pelo usuário
    public String getRecommendation() {
        if (preferences.isEmpty()) {
            return null;
        }
        Movie top = preferences.getRoot().getValue();
        return top.getCategory();
    }

    // --- Getters ---

    public LRUCache getCache() {
        return cache;
    }

    public SplayTree getPreferences() {
        return preferences;
    }

    public String getName() {
        return name;
    }

    public List<Movie> getRecentMovies(int n) {
        return cache.getRecentMovies(n);
    }

    public List<Integer> getEvictionHistory() {
        return cache.getEvictionHistory();
    }
}
