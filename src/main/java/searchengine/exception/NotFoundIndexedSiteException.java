package searchengine.exception;

public class NotFoundIndexedSiteException extends RuntimeException {
    public NotFoundIndexedSiteException(String message) {
        super(message, null, false, false);
    }
}
