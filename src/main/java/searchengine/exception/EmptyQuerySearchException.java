package searchengine.exception;

public class EmptyQuerySearchException extends RuntimeException {
    public EmptyQuerySearchException(String message) {
        super(message, null, false, false);
    }
}
