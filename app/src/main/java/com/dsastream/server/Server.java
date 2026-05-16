package com.dsastream.server;

import com.dsastream.model.Movie;
import com.dsastream.server.ds.ListNode;
import com.dsastream.server.ds.HashTable;
import com.dsastream.server.ds.LinkedList;
import com.dsastream.server.ds.TitlePrefixIndex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private LinkedList database;
    private HashTable index;
    private TitlePrefixIndex titleIndex;

    // 509 was chosen as the size of the hash table because it is the closest prime number to 2^5 (512)
    private static final int TABLE_SIZE = 509;

    public Server() {
        this.database = new LinkedList();
        this.index = new HashTable(TABLE_SIZE);
        this.titleIndex = new TitlePrefixIndex(TABLE_SIZE);
    }

    public void loadInitialData() {
        System.out.println("[SERVIDOR]: Carregando catálogo de filmes via CSV...");

        try (InputStream is = getClass().getResourceAsStream("/csv/movies_dataset.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {

            if (is == null) {
                System.out.println("[SERVIDOR]: Ficheiro CSV não encontrado!");
                return;
            }

            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // The data is split using semicolons
                String[] data = line.split(";");

                if (data.length >= 3) {
                    int id = Integer.parseInt(data[0].trim());
                    String title = data[1].trim();
                    String category = data[2].trim();

                    Movie movie = new Movie(id, title, category);

                    // Putting the data on database and indexes
                    ListNode insertedNode = database.add(movie);
                    index.put(id, insertedNode);
                    if (title.length() >= 3) {
                        String prefix = title.substring(0, 3).toLowerCase();
                        titleIndex.put(prefix, movie);
                    }
                }
            }
            System.out.println("[SERVIDOR]: Catálogo TMDB carregado com sucesso!");

        } catch (Exception e) {
            System.out.println("[SERVIDOR]: Erro ao ler o ficheiro CSV: " + e.getMessage());
        }
    }
    public Movie requestMovieWithoutIndex(int id) {
        System.out.println("\n--- [SERVIDOR]: Recebida requisição SEM índice para o ID " + id + " ---");
        return database.searchSequential(id);
    }

    public Movie requestMovieWithIndex(int id) {
        System.out.println("\n--- [SERVIDOR]: Recebida requisição COM índice para o ID " + id + " ---");
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

    // --- Getters and Setters ---

    public LinkedList getDatabase() {
        return database;
    }
}
