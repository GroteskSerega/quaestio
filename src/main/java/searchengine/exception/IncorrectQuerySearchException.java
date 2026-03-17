package searchengine.exception;

public class IncorrectQuerySearchException extends RuntimeException {
    public IncorrectQuerySearchException(String message) {
        super(message, null, false, false);
    }
}
