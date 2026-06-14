package com.dsaplus.common.ds;

import com.dsaplus.model.Movie;

public class SplayNode {
    private int key;
    private Movie value;
    private SplayNode left;
    private SplayNode right;

    public SplayNode(int key, Movie value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() { return key; }
    public void setKey(int key) { this.key = key; }

    public Movie getValue() { return value; }
    public void setValue(Movie value) { this.value = value; }

    public SplayNode getLeft() { return left; }
    public void setLeft(SplayNode left) { this.left = left; }

    public SplayNode getRight() { return right; }
    public void setRight(SplayNode right) { this.right = right; }
}
