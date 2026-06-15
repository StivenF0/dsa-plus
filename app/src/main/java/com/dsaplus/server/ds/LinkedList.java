package com.dsaplus.server.ds;

import com.dsaplus.model.Movie;
import com.dsaplus.util.Logger;

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

    // --- Getters e Setters ---

    public ListNode getHead() {
        return head;
    }
}
