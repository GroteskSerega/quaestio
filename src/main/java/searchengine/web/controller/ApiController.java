package searchengine.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.service.ApisService;
import searchengine.service.SearchService;
import searchengine.service.StatisticsService;
import searchengine.web.dto.api.IndexingResponse;
import searchengine.web.dto.api.search.ResponseSearchContainer;
import searchengine.web.dto.api.statistics.StatisticsResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final ApisService apisService;
    private final SearchService searchService;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(
                statisticsService.getStatistics()
        );
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexingResponse> startIndexing() {
        return ResponseEntity.accepted()
                .body(apisService.startIndexing());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexingResponse> stopIndexing() {
        return ResponseEntity.ok(
                apisService.stopIndexing()
        );
    }

    @PostMapping("/indexPage")
    public ResponseEntity<IndexingResponse> indexPage(@RequestParam String url) {
        return ResponseEntity.accepted().body(
                apisService.indexPage(url)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseSearchContainer> search(@RequestParam(required = false) String query,
                                                          @RequestParam(required = false, defaultValue = "0") Integer offset,
                                                          @RequestParam(required = false, defaultValue = "20") Integer limit,
                                                          @RequestParam(required = false) String site) {
        return ResponseEntity.ok(
                searchService.search(query,
                        offset,
                        limit,
                        site)
        );
    }
}
