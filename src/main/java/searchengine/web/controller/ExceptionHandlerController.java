package searchengine.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import searchengine.exception.*;
import searchengine.web.dto.api.IndexingResponse;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler({
            IncorrectQuerySearchException.class,
            NotFoundIndexedSiteException.class,
            EmptyQuerySearchException.class,
            PageNotRelatedForSiteException.class,
            NotFoundSiteException.class,
            SiteStatusIncorrect.class})
    public ResponseEntity<IndexingResponse> handleBusinessExceptions(IncorrectQuerySearchException ex) {
        return ResponseEntity.badRequest()
                .body(new IndexingResponse(false,
                        ex.getLocalizedMessage()));
    }
}
