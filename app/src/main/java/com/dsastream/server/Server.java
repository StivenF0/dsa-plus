package com.dsastream.server;

import com.dsastream.model.Movie;
import com.dsastream.server.ds.ListNode;
import com.dsastream.server.ds.HashTable;
import com.dsastream.server.ds.LinkedList;
import com.dsastream.server.ds.TitlePrefixIndex;
import com.dsastream.common.ds.SplayNode;
import com.dsastream.common.ds.SplayTree;
import com.dsastream.util.CsvParser;
import com.dsastream.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Server {
    private final LinkedList database;
    private final HashTable index;
    private final TitlePrefixIndex titleIndex;
    private final HashMap<String, List<Movie>> categoryIndex;
    private final SplayTree popularity;

    // 509 foi escolhido como o tamanho da tabela hash por ser o número primo mais próximo de 2^9 (512)
    private static final int TABLE_SIZE = 509;

    public Server() {
        this.database = new LinkedList();
        this.index = new HashTable(TABLE_SIZE);
        this.titleIndex = new TitlePrefixIndex(TABLE_SIZE);
        this.categoryIndex = new HashMap<>();
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
            if (title.length() >= 3) {
                String prefix = title.substring(0, 3).toLowerCase();
                titleIndex.put(prefix, movie);
            }

            String categoryKey = category.toLowerCase().trim();
            List<Movie> categoryMovies = categoryIndex.get(categoryKey);
            if (categoryMovies == null) {
                categoryMovies = new ArrayList<>();
                categoryIndex.put(categoryKey, categoryMovies);
            }
            categoryMovies.add(movie);
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

    public List<Movie> requestMoviesByTitle(String fragment) {
        Logger.info("Server", "Busca por título: \"" + fragment + "\"");
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
                Logger.debug("Server", "Busca por prefixo \"" + fragment + "\". Candidatos = " + candidates.size() + ", Filtrados = " + filteredCount + ", Resultados = " + results.size());
                return results;
            }
        }

        // Fallback para busca sequencial se o fragmento for < 3 ou o prefixo não for encontrado
        return database.searchByTitleFragment(fragment);
    }

    public int getTotalMoviesInCategory(String category) {
        List<Movie> movies = categoryIndex.get(category.toLowerCase().trim());
        return movies != null ? movies.size() : 0;
    }

    public List<Movie> requestMoviesByCategory(String category, int page, int pageSize) {
        List<Movie> allMovies = categoryIndex.get(category.toLowerCase().trim());

        if (allMovies == null || allMovies.isEmpty()) {
            return new ArrayList<>();
        }

        int startIndex = (page - 1) * pageSize;
        if (startIndex >= allMovies.size() || startIndex < 0) {
            return new ArrayList<>();
        }

        int endIndex = Math.min(startIndex + pageSize, allMovies.size());
        return allMovies.subList(startIndex, endIndex);
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
