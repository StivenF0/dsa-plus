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

    private int hashFunction(String key) {
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
                if (current.prefix.equals(normalizedPrefix)) {
                    current.movies.add(movie);
                    return;
                }
                if (current.next == null) break;
                current = current.next;
            }
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

