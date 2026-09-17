package model;

/**
 * Represents a book in the library catalog.
 * Demonstrates encapsulation: all fields private, accessed via getters/setters.
 */
public class Book {
    private String isbn;
    private String title;
    private String author;
    private String genre;
    private int totalCopies;
    private int availableCopies;

    public Book(String isbn, String title, String author, String genre, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    /** Serialize to a CSV line for file persistence. */
    public String toCsv() {
        return String.join(",", isbn, escape(title), escape(author), escape(genre),
                String.valueOf(totalCopies), String.valueOf(availableCopies));
    }

    private String escape(String s) {
        return s.replace(",", ";");
    }

    public static Book fromCsv(String line) {
        String[] parts = line.split(",", -1);
        Book b = new Book(parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4]));
        b.setAvailableCopies(Integer.parseInt(parts[5]));
        return b;
    }

    @Override
    public String toString() {
        return String.format("%-10s | %-25s | %-18s | %-12s | Total:%-3d Available:%-3d",
                isbn, title, author, genre, totalCopies, availableCopies);
    }
}
