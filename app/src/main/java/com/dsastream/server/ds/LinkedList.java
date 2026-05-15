package com.dsastream.server.ds;

import com.dsastream.model.Movie;

public class LinkedList {
    private ListNode head;

    public LinkedList() {
        this.head = null;
    }

    // Adds a movie to the LinkedList and returns it
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

    // Searches for a movie by ID
    public Movie searchSequential(int id) {
        if (id <= 0) {
            System.out.println("Busca rejeitada. ID inválido: " + id);
            return null;
        }

        int comparisions = 0;
        ListNode current = head;

        while (current != null) {
            comparisions++;

            if (current.getMovie().getId() == id) {
                System.out.println("Busca sequencial finalizada. Comparações = " + comparisions);
                return current.getMovie();
            }

            current = current.getNext();
        }

        System.out.println("Filme não encontrado. Comparações = " + comparisions);
        return null;
    }

    public ListNode getHead() {
        return head;
    }
}
