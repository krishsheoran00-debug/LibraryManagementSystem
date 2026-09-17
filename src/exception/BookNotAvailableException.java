package exception;

/** Thrown when a member tries to issue a book with no available copies. */
public class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String message) {
        super(message);
    }
}
