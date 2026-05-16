package com.dsastream.client;

import com.dsastream.client.ds.AVLTree;
import com.dsastream.client.ds.CacheQueue;
import com.dsastream.model.Movie;

public class Client {
    private final AVLTree cacheTree;
    private final CacheQueue evictionQueue;

    @SuppressWarnings("FieldCanBeLocal")
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

        // Verifica se o filme já está no cache para evitar duplicatas
        if (cacheTree.search(id) != null) {
            System.out.println("\n--- [CLIENTE]: O filme '" + movie.getTitle() + "' já está no cache. ---");
            return;
        }

        // Se o cache estiver cheio, remove o filme mais antigo antes de adicionar o novo
        if (evictionQueue.getSize() >= MAX_CACHE_SIZE) {
            int oldestId = evictionQueue.dequeue();
            cacheTree.remove(oldestId);
            System.out.println("\n--- [CLIENTE]: Cache cheio! Removendo o filme ID " + oldestId + " ---");
        }

        // Adiciona o novo filme ao cache e atualiza a fila de expulsão
        cacheTree.insert(id, movie);
        evictionQueue.enqueue(id);
        System.out.println("\n--- [CLIENTE]: Filme '[" + movie.getId() + "] " + movie.getTitle() + "' adicionado ao cache. ---");
    }

    // --- Getters e Setters ---

    public AVLTree getCacheTree() {
        return cacheTree;
    }
}