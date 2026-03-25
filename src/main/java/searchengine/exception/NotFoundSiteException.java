package searchengine.exception;

public class NotFoundSiteException extends RuntimeException {
    public NotFoundSiteException(String message) {
        super(message, null, false, false);
    }
}
