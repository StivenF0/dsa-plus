package com.dsaplus.model;

public class Movie {

    private int id;
    private String title;
    private String category;
    private int year;
    private String synopsis;

    public Movie() {}

    public Movie(int id, String title, String category, int year, String synopsis) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.year = year;
        this.synopsis = synopsis;
    }

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public int getYear() {
        return year;
    }

    public String getSynopsis() {
        return synopsis;
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

    public void setYear(int year) {
        this.year = year;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    @Override
    public String toString() {
        return "[ID: " + id + "] " + title + " (" + category + ", " + year + ")";
    }
}
