package model;

import java.time.LocalDate;

/**
 * Represents a single issue/return transaction.
 */
public class Transaction {
    private String transactionId;
    private String isbn;
    private String memberId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // null if not yet returned
    private double fineCharged;

    public Transaction(String transactionId, String isbn, String memberId,
                        LocalDate issueDate, LocalDate dueDate) {
        this.transactionId = transactionId;
        this.isbn = isbn;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.fineCharged = 0.0;
    }

    public String getTransactionId() { return transactionId; }
    public String getIsbn() { return isbn; }
    public String getMemberId() { return memberId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public double getFineCharged() { return fineCharged; }
    public void setFineCharged(double fineCharged) { this.fineCharged = fineCharged; }
    public boolean isReturned() { return returnDate != null; }

    public String toCsv() {
        return String.join(",",
                transactionId, isbn, memberId,
                issueDate.toString(), dueDate.toString(),
                returnDate == null ? "NULL" : returnDate.toString(),
                String.valueOf(fineCharged));
    }

    public static Transaction fromCsv(String line) {
        String[] p = line.split(",", -1);
        Transaction t = new Transaction(p[0], p[1], p[2], LocalDate.parse(p[3]), LocalDate.parse(p[4]));
        if (!p[5].equals("NULL")) t.setReturnDate(LocalDate.parse(p[5]));
        t.setFineCharged(Double.parseDouble(p[6]));
        return t;
    }

    @Override
    public String toString() {
        return String.format("%-6s | ISBN:%-10s | Member:%-8s | Issued:%-10s | Due:%-10s | Returned:%-10s | Fine:Rs.%.2f",
                transactionId, isbn, memberId, issueDate, dueDate,
                returnDate == null ? "PENDING" : returnDate.toString(), fineCharged);
    }
}
