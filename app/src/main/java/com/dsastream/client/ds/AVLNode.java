package com.dsastream.client.ds;

import com.dsastream.model.Movie;

public class AVLNode {
    private int id;
    private Movie movie;
    private AVLNode left, right;
    private int height;

    public AVLNode() {}

    public AVLNode(int id, Movie movie) {
        this.id = id;
        this.movie = movie;
        this.left = null;
        this.right = null;
        this.height = 0;
    }

    // --- Setters and Getters ---

    public int getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public AVLNode getLeft() {
        return left;
    }

    public AVLNode getRight() {
        return right;
    }

    public int getHeight() {
        return height;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void setLeft(AVLNode left) {
        this.left = left;
    }

    public void setRight(AVLNode right) {
        this.right = right;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
