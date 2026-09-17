package exception;

/** Thrown for invalid transaction operations, e.g. returning a book that was never issued. */
public class InvalidTransactionException extends Exception {
    public InvalidTransactionException(String message) {
        super(message);
    }
}
