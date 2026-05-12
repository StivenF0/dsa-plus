package com.dsastream.server;

import com.dsastream.model.Movie;
import com.dsastream.model.ListNode;
import com.dsastream.server.ds.HashTable;
import com.dsastream.server.ds.LinkedList;

public class Server {
    private LinkedList database;
    private HashTable index;

    public Server() {
        this.database = new LinkedList();
        // 1021 was chosen as the size of the hash table because it is the closest prime number to 2^10 (1024)
        this.index = new HashTable(1021);
    }

    public void loadInitialData() {
        System.out.println("Servidor: Carregando 1000 filmes na base de dados...");

        for (int i = 1; i <= 1000; i++) {
            String title = "Movie Title " + i;
            String category = "Category " + ((i % 5) + 1); // Gera 5 categorias diferentes
            Movie movie = new Movie(i, title, category);
            ListNode insertedListNode = database.add(movie);
            index.put(i, insertedListNode);
        }

        System.out.println("Servidor: Catálogo carregado com sucesso!");
    }

    public Movie requestMovieWithoutIndex(int id) {
        System.out.println("\n--- Servidor: Recebida requisição SEM índice para o ID " + id + " ---");
        return database.searchSequential(id);
    }

    public Movie requestMovieWithIndex(int id) {
        System.out.println("\n--- Servidor: Recebida requisição COM índice para o ID " + id + " ---");
        ListNode foundListNode = index.searchIndexed(id);

        if (foundListNode != null) {
            return foundListNode.getMovie();
        }
        return null;
    }
}
