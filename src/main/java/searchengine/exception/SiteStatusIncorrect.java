package searchengine.exception;

public class SiteStatusIncorrect extends RuntimeException {
    public SiteStatusIncorrect(String message) {
        super(message, null, false, false);
    }
}
