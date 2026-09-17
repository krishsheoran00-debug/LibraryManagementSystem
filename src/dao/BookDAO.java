package dao;

import model.Book;
import util.FileHandler;
import util.AppLogger;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data Access Object for books. Uses a HashMap (keyed by ISBN) for O(1)
 * lookup performance, backed by a CSV file for persistence between runs.
 */
public class BookDAO {
    private static final String FILE_PATH = "data/books.csv";
    private final Map<String, Book> catalog = new LinkedHashMap<>();

    public BookDAO() {
        load();
    }

    private void load() {
        try {
            for (String line : FileHandler.readLines(FILE_PATH)) {
                Book b = Book.fromCsv(line);
                catalog.put(b.getIsbn(), b);
            }
        } catch (IOException e) {
            AppLogger.error("Failed to load books: " + e.getMessage());
        }
    }

    private void persist() {
        try {
            java.util.List<String> lines = new java.util.ArrayList<>();
            for (Book b : catalog.values()) lines.add(b.toCsv());
            FileHandler.writeLines(FILE_PATH, lines);
        } catch (IOException e) {
            AppLogger.error("Failed to save books: " + e.getMessage());
        }
    }

    public void addBook(Book book) {
        catalog.put(book.getIsbn(), book);
        persist();
    }

    public Book findByIsbn(String isbn) {
        return catalog.get(isbn);
    }

    public boolean deleteBook(String isbn) {
        boolean removed = catalog.remove(isbn) != null;
        if (removed) persist();
        return removed;
    }

    public void updateBook(Book book) {
        catalog.put(book.getIsbn(), book);
        persist();
    }

    public Map<String, Book> getAllBooks() {
        return catalog;
    }

    public java.util.List<Book> searchByTitleOrAuthor(String keyword) {
        java.util.List<Book> results = new java.util.ArrayList<>();
        String lower = keyword.toLowerCase();
        for (Book b : catalog.values()) {
            if (b.getTitle().toLowerCase().contains(lower) || b.getAuthor().toLowerCase().contains(lower)) {
                results.add(b);
            }
        }
        return results;
    }
}
