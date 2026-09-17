package service;

import dao.BookDAO;
import dao.MemberDAO;
import dao.TransactionDAO;
import exception.BookNotAvailableException;
import exception.BookNotFoundException;
import exception.InvalidTransactionException;
import exception.MemberNotFoundException;
import model.Book;
import model.Member;
import model.Transaction;
import util.AppLogger;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Core business-logic layer. Coordinates the three DAOs and enforces
 * library rules (availability checks, fine calculation, borrowing limits).
 * Implements Searchable and Reportable to demonstrate interface use.
 */
public class LibraryService implements Searchable, Reportable {
    private static final int LOAN_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 5.0; // currency units per day late
    private static final int MAX_BOOKS_PER_MEMBER = 3;

    private final BookDAO bookDAO;
    private final MemberDAO memberDAO;
    private final TransactionDAO transactionDAO;
    private int transactionCounter;

    public LibraryService(BookDAO bookDAO, MemberDAO memberDAO, TransactionDAO transactionDAO) {
        this.bookDAO = bookDAO;
        this.memberDAO = memberDAO;
        this.transactionDAO = transactionDAO;
        this.transactionCounter = transactionDAO.getAllTransactions().size();
    }

    // ---------- Book management ----------

    public void addBook(Book book) {
        bookDAO.addBook(book);
        AppLogger.info("Book added: " + book.getIsbn() + " - " + book.getTitle());
    }

    public boolean deleteBook(String isbn) {
        boolean ok = bookDAO.deleteBook(isbn);
        AppLogger.info("Book delete attempt " + isbn + " success=" + ok);
        return ok;
    }

    public Book getBook(String isbn) throws BookNotFoundException {
        Book b = bookDAO.findByIsbn(isbn);
        if (b == null) throw new BookNotFoundException("No book found with ISBN " + isbn);
        return b;
    }

    @Override
    public List<Book> search(String keyword) {
        return bookDAO.searchByTitleOrAuthor(keyword);
    }

    // ---------- Member management ----------

    public void addMember(Member member) {
        memberDAO.addMember(member);
        AppLogger.info("Member added: " + member.getId() + " - " + member.getName());
    }

    public boolean deleteMember(String id) {
        boolean ok = memberDAO.deleteMember(id);
        AppLogger.info("Member delete attempt " + id + " success=" + ok);
        return ok;
    }

    public Member getMember(String id) throws MemberNotFoundException {
        Member m = memberDAO.findById(id);
        if (m == null) throw new MemberNotFoundException("No member found with ID " + id);
        return m;
    }

    // ---------- Issue / Return ----------

    public Transaction issueBook(String isbn, String memberId)
            throws BookNotFoundException, MemberNotFoundException, BookNotAvailableException, InvalidTransactionException {

        Book book = getBook(isbn);
        Member member = getMember(memberId);

        if (!book.isAvailable()) {
            throw new BookNotAvailableException("Book '" + book.getTitle() + "' has no available copies.");
        }
        if (member.getBooksCurrentlyIssued() >= MAX_BOOKS_PER_MEMBER) {
            throw new InvalidTransactionException(
                    "Member " + member.getName() + " has reached the borrowing limit of " + MAX_BOOKS_PER_MEMBER);
        }
        if (member.getOutstandingFine() > 0) {
            throw new InvalidTransactionException(
                    "Member " + member.getName() + " has an outstanding fine of Rs." + member.getOutstandingFine()
                            + ". Please clear dues before borrowing.");
        }

        transactionCounter++;
        String txId = "T" + String.format("%04d", transactionCounter);
        LocalDate today = LocalDate.now();
        Transaction t = new Transaction(txId, isbn, memberId, today, today.plusDays(LOAN_PERIOD_DAYS));

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookDAO.updateBook(book);

        member.incrementIssued();
        memberDAO.updateMember(member);

        transactionDAO.addTransaction(t);
        AppLogger.info("Issued " + isbn + " to member " + memberId + " (txn " + txId + ")");
        return t;
    }

    public Transaction returnBook(String isbn, String memberId)
            throws BookNotFoundException, MemberNotFoundException, InvalidTransactionException {

        Book book = getBook(isbn);
        Member member = getMember(memberId);

        Transaction t = transactionDAO.findOpenTransaction(isbn, memberId);
        if (t == null) {
            throw new InvalidTransactionException(
                    "No open loan found for ISBN " + isbn + " and member " + memberId);
        }

        LocalDate today = LocalDate.now();
        t.setReturnDate(today);

        long daysLate = ChronoUnit.DAYS.between(t.getDueDate(), today);
        double fine = 0.0;
        if (daysLate > 0) {
            fine = daysLate * FINE_PER_DAY;
            t.setFineCharged(fine);
            member.addFine(fine);
        }

        transactionDAO.updateTransaction(t);

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookDAO.updateBook(book);

        member.decrementIssued();
        memberDAO.updateMember(member);

        AppLogger.info("Returned " + isbn + " from member " + memberId +
                (fine > 0 ? " with fine Rs." + fine : ""));
        return t;
    }

    public void clearMemberFine(String memberId) throws MemberNotFoundException {
        Member m = getMember(memberId);
        m.clearFine();
        memberDAO.updateMember(m);
        AppLogger.info("Cleared fine for member " + memberId);
    }

    // ---------- Reports ----------

    @Override
    public void printReport() {
        printOverdueReport();
        System.out.println();
        printMostBorrowedReport();
    }

    public List<Transaction> getOverdueTransactions() {
        return transactionDAO.findOverdue(LocalDate.now());
    }

    public void printOverdueReport() {
        List<Transaction> overdue = getOverdueTransactions();
        System.out.println("=== OVERDUE BOOKS REPORT ===");
        if (overdue.isEmpty()) {
            System.out.println("No overdue books. Nice!");
            return;
        }
        for (Transaction t : overdue) {
            System.out.println(t);
        }
    }

    public void printMostBorrowedReport() {
        Map<String, Long> countByIsbn = new HashMap<>();
        for (Transaction t : transactionDAO.getAllTransactions()) {
            countByIsbn.merge(t.getIsbn(), 1L, Long::sum);
        }
        List<Map.Entry<String, Long>> sorted = new ArrayList<>(countByIsbn.entrySet());
        sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        System.out.println("=== MOST BORROWED BOOKS ===");
        if (sorted.isEmpty()) {
            System.out.println("No borrowing history yet.");
            return;
        }
        int rank = 1;
        for (Map.Entry<String, Long> entry : sorted) {
            Book b = bookDAO.findByIsbn(entry.getKey());
            String title = (b != null) ? b.getTitle() : "(deleted book " + entry.getKey() + ")";
            System.out.printf("%d. %s — borrowed %d time(s)%n", rank++, title, entry.getValue());
        }
    }

    public void printMemberActivityReport(String memberId) throws MemberNotFoundException {
        Member m = getMember(memberId);
        System.out.println("=== ACTIVITY REPORT: " + m.getName() + " (" + memberId + ") ===");
        for (Transaction t : transactionDAO.getAllTransactions()) {
            if (t.getMemberId().equals(memberId)) {
                System.out.println(t);
            }
        }
        System.out.println("Currently issued: " + m.getBooksCurrentlyIssued());
        System.out.println("Outstanding fine: Rs." + m.getOutstandingFine());
    }
}
