package searchengine.service;

import searchengine.web.dto.api.IndexingResponse;

public interface ApisService {
    IndexingResponse startIndexing();
    IndexingResponse stopIndexing();
    IndexingResponse indexPage(String url);
}
