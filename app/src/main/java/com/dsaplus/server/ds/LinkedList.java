package com.dsaplus.server.ds;

import com.dsaplus.model.Movie;
import com.dsaplus.util.Logger;
import java.util.ArrayList;
import java.util.List;

public class LinkedList {

    private ListNode head;

    public LinkedList() {
        this.head = null;
    }

    // Adiciona um filme à lista ligada e o retorna
    public ListNode add(Movie movie) {
        ListNode newListNode = new ListNode(movie);

        if (head == null) {
            head = newListNode;
        } else {
            ListNode current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newListNode);
        }

        return newListNode;
    }

    // Busca um filme pelo ID
    public Movie searchSequential(int id) {
        if (id <= 0) {
            Logger.warn("LinkedList", "Busca rejeitada. ID inválido: " + id);
            return null;
        }

        int comparisions = 0;
        ListNode current = head;

        while (current != null) {
            comparisions++;

            if (current.getMovie().getId() == id) {
                Logger.debug("LinkedList", "Busca sequencial ID " + id + ". Comparações = " + comparisions);
                return current.getMovie();
            }

            current = current.getNext();
        }

        Logger.debug("LinkedList", "Filme ID " + id + " não encontrado. Comparações = " + comparisions);
        return null;
    }

    // Busca filmes cujo título contém o fragmento fornecido (ignora maiúsculas/minúsculas)
    public List<Movie> searchByTitleFragment(String fragment) {
        List<Movie> results = new ArrayList<>();
        String normalizedFragment = fragment.toLowerCase().trim();
        ListNode current = head;
        int comparisons = 0;

        while (current != null) {
            comparisons++;
            if (current.getMovie().getTitle().toLowerCase().contains(normalizedFragment)) {
                results.add(current.getMovie());
            }
            current = current.getNext();
        }
        Logger.debug("LinkedList", "Busca por trecho \"" + fragment + "\". Comparações = " + comparisons + ", resultados = " + results.size());
        return results;
    }

    // --- Getters e Setters ---

    public ListNode getHead() {
        return head;
    }
}
