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

    public String toDataString() {
        return id + "|" + escape(title) + "|" + escape(category) + "|" + year + "|" + escape(synopsis);
    }

    public static Movie fromDataString(String data) {
        String[] parts = data.split("(?<!\\\\)\\|", 5);
        if (parts.length < 5) return null;
        try {
            int id = Integer.parseInt(parts[0].trim());
            String title = unescape(parts[1]);
            String category = unescape(parts[2]);
            int year = Integer.parseInt(parts[3].trim());
            String synopsis = parts.length > 4 ? unescape(parts[4]) : "";
            return new Movie(id, title, category, year, synopsis);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("|", "\\|");
    }

    private static String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\\' && i + 1 < s.length()) {
                sb.append(s.charAt(i + 1));
                i++;
            } else {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString().trim();
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
