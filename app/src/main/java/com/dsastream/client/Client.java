package com.dsastream.client;

import com.dsastream.client.ds.AVLTree;
import com.dsastream.client.ds.CacheQueue;
import com.dsastream.model.Movie;

public class Client {
    private AVLTree cacheTree;
    private CacheQueue evictionQueue;
    private final int MAX_CACHE_SIZE = 50;

    public Client() {
        this.cacheTree = new AVLTree();
        this.evictionQueue = new CacheQueue();
    }

    public void viewMovie(Movie movie) {
        if (movie == null) {
            System.out.println("\n--- [CLIENTE]: Erro! Título não encontrado no servidor. ---");
            return;
        }

        System.out.println("\n--- [CLIENTE]: Preparando para exibir '" + movie.getTitle() + "' ---");
        System.out.println(movie);
        addToCache(movie);
    }

    public void addToCache(Movie movie) {
        System.out.println("\n--- [CLIENTE]: Adicionando '" + movie.getTitle() + "' ao cache ---");
        int id = movie.getId();

        // Checks if the movie is already in the cache to avoid duplicates
        if (cacheTree.search(id) != null) {
            System.out.println("\n--- [CLIENTE]: O filme '" + movie.getTitle() + "' já está no cache. ---");
            return;
        }

        // If the cache is full, remove the oldest movie before adding the new one
        if (evictionQueue.getSize() >= MAX_CACHE_SIZE) {
            int oldestId = evictionQueue.dequeue();
            cacheTree.remove(oldestId);
            System.out.println("\n--- [CLIENTE]: Cache cheio! Removendo o filme ID " + oldestId + " ---");
        }

        // Add the new movie to the cache and update the eviction queue
        cacheTree.insert(id, movie);
        evictionQueue.enqueue(id);
        System.out.println("\n--- [CLIENTE]: Filme '" + movie.getTitle() + "' adicionado ao cache. ---");
    }

    // --- Getters and Setters ---

    public AVLTree getCacheTree() {
        return cacheTree;
    }
}