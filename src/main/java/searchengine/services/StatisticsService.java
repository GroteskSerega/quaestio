package searchengine.services;

import org.springframework.http.ResponseEntity;

public interface StatisticsService<T> {
    ResponseEntity<T> getStatistics();
}
