package com.dsastream.client;

import com.dsastream.client.ds.LRUCache;
import com.dsastream.common.ds.SplayTree;
import com.dsastream.model.Movie;

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
            System.out.println("\n--- [" + name + "]: Erro! Título não encontrado no servidor. ---");
            return;
        }

        System.out.println("\n--- [" + name + "]: Preparando para exibir '" + movie.getTitle() + "' ---");
        System.out.println(movie);
        addToCache(movie);
        preferences.insert(movie.getId(), movie);
    }

    public void addToCache(Movie movie) {
        System.out.println("\n--- [" + name + "]: Adicionando '" + movie.getTitle() + "' ao cache ---");
        cache.put(movie.getId(), movie);
        System.out.println("\n--- [" + name + "]: Filme '[" + movie.getId() + "] " + movie.getTitle() + "' adicionado ao cache. ---");
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
