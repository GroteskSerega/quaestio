package searchengine.services;

import org.springframework.http.ResponseEntity;

public interface ApisService<T> {
    ResponseEntity<T> startIndexing();
    ResponseEntity<T> stopIndexing();
}
