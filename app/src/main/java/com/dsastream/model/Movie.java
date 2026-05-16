package com.dsastream.model;

public class Movie {
    private int id;
    private String title;
    private String category;

    public Movie(int id, String title, String category) {
        this.id = id;
        this.title = title;
        this.category = category;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "[ID: " + id + "] " + title + " (" + category + ")";
    }
}
