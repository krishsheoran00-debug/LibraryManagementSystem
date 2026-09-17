package exception;

/** Thrown when an operation references an ISBN that doesn't exist in the catalog. */
public class BookNotFoundException extends Exception {
    public BookNotFoundException(String message) {
        super(message);
    }
}
