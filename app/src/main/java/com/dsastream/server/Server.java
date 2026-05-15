package com.dsastream.server;

import com.dsastream.model.Movie;
import com.dsastream.server.ds.ListNode;
import com.dsastream.server.ds.HashTable;
import com.dsastream.server.ds.LinkedList;
import com.dsastream.server.ds.TitlePrefixIndex;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private LinkedList database;
    private HashTable index;
    private TitlePrefixIndex titleIndex;

    public Server() {
        this.database = new LinkedList();
        // 509 was chosen as the size of the hash table because it is the closest prime number to 2^5 (512)
        this.index = new HashTable(509);
        this.titleIndex = new TitlePrefixIndex(509);
    }

    public void loadInitialData() {
        System.out.println("Servidor: Carregando 1000 filmes na base de dados...");

        for (int i = 1; i <= 1000; i++) {
            String title = "Movie Title " + i;
            String category = "Category " + ((i % 5) + 1);
            Movie movie = new Movie(i, title, category);
            ListNode insertedListNode = database.add(movie);
            index.put(i, insertedListNode);

            // Indexing by prefix of 3 characters
            if (title.length() >= 3) {
                String prefix = title.substring(0, 3).toLowerCase();
                titleIndex.put(prefix, movie);
            }
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

    public List<Movie> requestMoviesByTitle(String fragment) {
        System.out.println("\n--- Servidor: Recebida requisição de busca por título: \"" + fragment + "\" ---");
        String normalized = fragment.toLowerCase().trim();

        if (normalized.length() >= 3) {
            String prefix = normalized.substring(0, 3);
            List<Movie> candidates = titleIndex.get(prefix);

            if (candidates != null) {
                List<Movie> results = new ArrayList<>();
                int filteredCount = 0;
                for (Movie m : candidates) {
                    if (m.getTitle().toLowerCase().contains(normalized)) {
                        results.add(m);
                    }
                    filteredCount++;
                }
                System.out.println("Busca por índice de prefixo finalizada. Candidatos = " + candidates.size() + ", Filtrados = " + filteredCount + ", Resultados = " + results.size());
                return results;
            }
        }

        // Fallback to sequential search if fragment < 3 or prefix not found
        return database.searchByTitleFragment(fragment);
    }
}
