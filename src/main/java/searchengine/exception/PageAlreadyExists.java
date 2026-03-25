package searchengine.exception;

public class PageAlreadyExists extends RuntimeException {
    public PageAlreadyExists(String message) {
        super(message, null, false, false);
    }
}
