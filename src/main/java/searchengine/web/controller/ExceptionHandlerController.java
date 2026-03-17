package searchengine.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import searchengine.exception.*;
import searchengine.web.dto.api.IndexingResponse;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(IncorrectQuerySearchException.class)
    public ResponseEntity<IndexingResponse> incorrectQuerySearch(IncorrectQuerySearchException ex) {
        return ResponseEntity.badRequest()
                .body(new IndexingResponse(false,
                        ex.getLocalizedMessage()));
    }

    @ExceptionHandler(NotFoundIndexedSiteException.class)
    public ResponseEntity<IndexingResponse> notFoundIndexedSite(NotFoundIndexedSiteException ex) {
        return ResponseEntity.badRequest()
                .body(new IndexingResponse(false,
                        ex.getLocalizedMessage()));
    }

    @ExceptionHandler(EmptyQuerySearchException.class)
    public ResponseEntity<IndexingResponse> emptyQuerySearch(EmptyQuerySearchException ex) {
        return ResponseEntity.badRequest()
                .body(new IndexingResponse(false,
                        ex.getLocalizedMessage()));
    }

    @ExceptionHandler(PageNotRelatedForSiteException.class)
    public ResponseEntity<IndexingResponse> pageNotRelatedForSite(PageNotRelatedForSiteException ex) {
        return ResponseEntity.badRequest()
                .body(new IndexingResponse(false,
                        ex.getLocalizedMessage()));
    }
}
