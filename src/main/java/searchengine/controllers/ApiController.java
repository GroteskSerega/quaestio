package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.ResponseBody;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.ApisService;
import searchengine.services.StatisticsService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final ApisService apisService;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
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
}
