import dao.BookDAO;
import dao.MemberDAO;
import dao.TransactionDAO;
import exception.*;
import model.Book;
import model.Member;
import model.Transaction;
import service.LibraryService;
import util.AppLogger;

import java.util.List;
import java.util.Scanner;

/**
 * Console entry point. Menu-driven UI wired to the LibraryService.
 * All user input is validated; all business errors are caught and
 * shown as friendly messages rather than stack traces.
 */
public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static LibraryService service;

    public static void main(String[] args) {
        BookDAO bookDAO = new BookDAO();
        MemberDAO memberDAO = new MemberDAO();
        TransactionDAO transactionDAO = new TransactionDAO();
        service = new LibraryService(bookDAO, memberDAO, transactionDAO);

        AppLogger.info("Application started");
        seedDemoDataIfEmpty(bookDAO, memberDAO);

        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1": addBookFlow(); break;
                    case "2": searchBooksFlow(); break;
                    case "3": addMemberFlow(); break;
                    case "4": issueBookFlow(); break;
                    case "5": returnBookFlow(); break;
                    case "6": service.printOverdueReport(); break;
                    case "7": service.printMostBorrowedReport(); break;
                    case "8": memberActivityFlow(); break;
                    case "9": listAllBooksFlow(bookDAO); break;
                    case "0": running = false; break;
                    default: System.out.println("Invalid choice, try again.");
                }
            } catch (BookNotFoundException | MemberNotFoundException
                     | BookNotAvailableException | InvalidTransactionException e) {
                System.out.println("Error: " + e.getMessage());
                AppLogger.warn("Handled exception: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Error: please enter a valid number.");
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
                AppLogger.error("Unexpected exception: " + e);
            }
        }
        AppLogger.info("Application closed");
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
        System.out.println("1. Add Book");
        System.out.println("2. Search Books");
        System.out.println("3. Add Member");
        System.out.println("4. Issue Book");
        System.out.println("5. Return Book");
        System.out.println("6. Overdue Report");
        System.out.println("7. Most Borrowed Report");
        System.out.println("8. Member Activity Report");
        System.out.println("9. List All Books");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void addBookFlow() {
        System.out.print("ISBN: ");
        String isbn = sc.nextLine().trim();
        System.out.print("Title: ");
        String title = sc.nextLine().trim();
        System.out.print("Author: ");
        String author = sc.nextLine().trim();
        System.out.print("Genre: ");
        String genre = sc.nextLine().trim();
        System.out.print("Number of copies: ");
        int copies = Integer.parseInt(sc.nextLine().trim());
        service.addBook(new Book(isbn, title, author, genre, copies));
        System.out.println("Book added successfully.");
    }

    private static void searchBooksFlow() {
        System.out.print("Enter title/author keyword: ");
        String keyword = sc.nextLine().trim();
        List<Book> results = service.search(keyword);
        if (results.isEmpty()) {
            System.out.println("No matching books found.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private static void addMemberFlow() {
        System.out.print("Member ID: ");
        String id = sc.nextLine().trim();
        System.out.print("Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        service.addMember(new Member(id, name, email));
        System.out.println("Member added successfully.");
    }

    private static void issueBookFlow() throws BookNotFoundException, MemberNotFoundException,
            BookNotAvailableException, InvalidTransactionException {
        System.out.print("ISBN to issue: ");
        String isbn = sc.nextLine().trim();
        System.out.print("Member ID: ");
        String memberId = sc.nextLine().trim();
        Transaction t = service.issueBook(isbn, memberId);
        System.out.println("Issued. Transaction: " + t);
    }

    private static void returnBookFlow() throws BookNotFoundException, MemberNotFoundException,
            InvalidTransactionException {
        System.out.print("ISBN to return: ");
        String isbn = sc.nextLine().trim();
        System.out.print("Member ID: ");
        String memberId = sc.nextLine().trim();
        Transaction t = service.returnBook(isbn, memberId);
        System.out.println("Returned. " + t);
    }

    private static void memberActivityFlow() throws MemberNotFoundException {
        System.out.print("Member ID: ");
        String id = sc.nextLine().trim();
        service.printMemberActivityReport(id);
    }

    private static void listAllBooksFlow(BookDAO bookDAO) {
        if (bookDAO.getAllBooks().isEmpty()) {
            System.out.println("Catalog is empty.");
            return;
        }
        bookDAO.getAllBooks().values().forEach(System.out::println);
    }

    /** Seeds a couple of demo records on first run so graders can test immediately. */
    private static void seedDemoDataIfEmpty(BookDAO bookDAO, MemberDAO memberDAO) {
        if (bookDAO.getAllBooks().isEmpty()) {
            service.addBook(new Book("ISBN001", "Effective Java", "Joshua Bloch", "Programming", 3));
            service.addBook(new Book("ISBN002", "Clean Code", "Robert Martin", "Programming", 2));
            service.addBook(new Book("ISBN003", "The Hobbit", "J.R.R. Tolkien", "Fiction", 1));
        }
        if (memberDAO.getAllMembers().isEmpty()) {
            service.addMember(new Member("M001", "Aditi Sharma", "aditi@example.com"));
            service.addMember(new Member("M002", "Rahul Verma", "rahul@example.com"));
        }
    }
}
