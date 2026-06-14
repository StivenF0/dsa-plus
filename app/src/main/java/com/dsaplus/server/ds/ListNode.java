package com.dsaplus.server.ds;

import com.dsaplus.model.Movie;

public class ListNode {
    private Movie movie;
    private ListNode next;

    public ListNode(Movie movie) {
        this.movie = movie;
        this.next = null;
    }

    // --- Getters e Setters ---

    public Movie getMovie() {
        return movie;
    }

    public ListNode getNext() {
        return next;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void setNext(ListNode next) {
        this.next = next;
    }
}
