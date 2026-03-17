package searchengine.exception;

public class PageNotRelatedForSiteException extends RuntimeException {
    public PageNotRelatedForSiteException(String message) {
        super(message, null, false, false);
    }
}
