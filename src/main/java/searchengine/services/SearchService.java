package searchengine.services;

import org.springframework.http.ResponseEntity;

public interface SearchService<T> {
    ResponseEntity<T> search(String query, Integer offset, Integer limit, String selectedSite);
}
