package com.dsastream.server.ds;

import com.dsastream.model.Movie;
import java.util.ArrayList;
import java.util.List;

public class TitlePrefixIndex {
    private static class HashEntry {
        String prefix;
        List<Movie> movies;
        HashEntry next;

        public HashEntry(String prefix) {
            this.prefix = prefix;
            this.movies = new ArrayList<>();
            this.next = null;
        }
    }

    private final HashEntry[] table;
    private final int size;

    public TitlePrefixIndex(int size) {
        this.size = size;
        this.table = new HashEntry[size];
    }

    // Função de hash simples para calcular o índice com base no prefixo
    private int hashFunction(String key) {
        /*
        * Inspirada na função de hash do Java String, que é amplamente utilizada e tem uma boa distribuição de hash
        * para strings.
        * Essa função é conhecida como Polynomial Rolling Hash, onde cada caractere é multiplicado por uma potência de
        * um número primo (neste caso, 31) e somado ao hash acumulado.
        * */
        int hash = 7;
        for (int i = 0; i < key.length(); i++) {
            hash = hash * 31 + key.charAt(i);
        }
        return Math.abs(hash) % size;
    }

    public void put(String prefix, Movie movie) {
        String normalizedPrefix = prefix.toLowerCase().trim();
        int index = hashFunction(normalizedPrefix);

        if (table[index] == null) {
            table[index] = new HashEntry(normalizedPrefix);
            table[index].movies.add(movie);
        } else {
            HashEntry current = table[index];
            while (true) {
                // Verifica se o prefixo já existe na lista encadeada
                if (current.prefix.equals(normalizedPrefix)) {
                    // Se o prefixo já existe, adiciona o filme à lista de filmes associada a esse prefixo
                    current.movies.add(movie);
                    return;
                }
                // Se chegar ao final da lista encadeada sem encontrar o prefixo, adiciona um novo HashEntry
                if (current.next == null) break;

                current = current.next;
            }
            // Adiciona um novo HashEntry para o prefixo e associa o filme a ele
            current.next = new HashEntry(normalizedPrefix);
            current.next.movies.add(movie);
        }
    }

    public List<Movie> get(String prefix) {
        String normalizedPrefix = prefix.toLowerCase().trim();
        int index = hashFunction(normalizedPrefix);
        HashEntry current = table[index];

        while (current != null) {
            if (current.prefix.equals(normalizedPrefix)) {
                return current.movies;
            }
            current = current.next;
        }
        return null;
    }
}

