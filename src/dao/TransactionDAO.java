package dao;

import model.Transaction;
import util.FileHandler;
import util.AppLogger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private static final String FILE_PATH = "data/transactions.csv";
    private final List<Transaction> transactions = new ArrayList<>();

    public TransactionDAO() {
        load();
    }

    private void load() {
        try {
            for (String line : FileHandler.readLines(FILE_PATH)) {
                transactions.add(Transaction.fromCsv(line));
            }
        } catch (IOException e) {
            AppLogger.error("Failed to load transactions: " + e.getMessage());
        }
    }

    private void persist() {
        try {
            List<String> lines = new ArrayList<>();
            for (Transaction t : transactions) lines.add(t.toCsv());
            FileHandler.writeLines(FILE_PATH, lines);
        } catch (IOException e) {
            AppLogger.error("Failed to save transactions: " + e.getMessage());
        }
    }

    public void addTransaction(Transaction t) {
        transactions.add(t);
        persist();
    }

    public void updateTransaction(Transaction updated) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getTransactionId().equals(updated.getTransactionId())) {
                transactions.set(i, updated);
                break;
            }
        }
        persist();
    }

    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    /** Finds the currently-open (not yet returned) transaction for a given ISBN + member. */
    public Transaction findOpenTransaction(String isbn, String memberId) {
        for (Transaction t : transactions) {
            if (t.getIsbn().equals(isbn) && t.getMemberId().equals(memberId) && !t.isReturned()) {
                return t;
            }
        }
        return null;
    }

    public List<Transaction> findOverdue(java.time.LocalDate today) {
        List<Transaction> overdue = new ArrayList<>();
        for (Transaction t : transactions) {
            if (!t.isReturned() && t.getDueDate().isBefore(today)) {
                overdue.add(t);
            }
        }
        return overdue;
    }
}
