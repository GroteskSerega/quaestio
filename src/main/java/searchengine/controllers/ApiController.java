package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.ResponseBody;
import searchengine.services.ApisService;
import searchengine.services.SearchService;
import searchengine.services.StatisticsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService<ResponseBody> statisticsService;
    private final ApisService<ResponseBody> apisService;
    private final SearchService<ResponseBody> searchService;

    @GetMapping("/statistics")
    public ResponseEntity<ResponseBody> statistics() {
        return statisticsService.getStatistics();
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<ResponseBody> startIndexing() {
        return apisService.startIndexing();
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<ResponseBody> stopIndexing() {
        return apisService.stopIndexing();
    }

    @PostMapping("/indexPage")
    public ResponseEntity<ResponseBody> indexPage(@RequestParam String url) {
        return apisService.indexPage(url);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseBody> search(@RequestParam(required = false) String query,
                                               @RequestParam(required = false, defaultValue = "0") Integer offset,
                                               @RequestParam(required = false, defaultValue = "20") Integer limit,
                                               @RequestParam(required = false) String site) {
        return searchService.search(query,
                offset,
                limit,
                site);
    }
}
