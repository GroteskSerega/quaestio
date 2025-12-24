package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.ResponseBody;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.ApisService;
import searchengine.services.StatisticsService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final ApisService apisService;

    @Autowired
    public ApiController(StatisticsService statisticsService,
                         ApisService apisService) {
        this.statisticsService = statisticsService;
        this.apisService = apisService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<ResponseBody> startIndexing() {
        return apisService.startIndexing();
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<ResponseBody> stopIndexing() {
        return apisService.stopIndexing();
    }
}
