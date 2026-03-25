package searchengine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.component.AsyncIndexPageComponent;
import searchengine.component.AsyncIndexingSitesComponent;
import searchengine.component.SitesComponent;
import searchengine.core.engine.TaskManagerEngine;
import searchengine.entity.Site;
import searchengine.entity.SiteStatusType;
import searchengine.exception.PageNotRelatedForSiteException;
import searchengine.web.dto.api.IndexingResponse;

import java.util.*;

import static searchengine.service.ServiceLoggingTemplates.*;
import static searchengine.web.dto.MessagesTemplates.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApisServiceImpl implements ApisService {

    private final SitesComponent sitesComponent;

    private final AsyncIndexingSitesComponent asyncIndexingSitesComponent;
    private final AsyncIndexPageComponent asyncIndexPageComponent;

    public IndexingResponse startIndexing() {
        log.info(TEMPLATE_SERVICE_API_REQUEST_START_INDEXING);
        List<Site> existingSites =
                sitesComponent.getExistingSitesFromDBAndMatchWithConfig();

        boolean isAlreadyIndexing = existingSites
                .stream()
                .anyMatch(site ->
                        site.getStatus() == SiteStatusType.INDEXING);

        if (isAlreadyIndexing) {
            return new IndexingResponse(true, TEMPLATE_API_INDEXING_ALREADY_STARTED);
        }

        asyncIndexingSitesComponent.startAsyncProcessIndexingSites(existingSites);

        return new IndexingResponse(true, null);
    }

    // TODO Лучше опрашивать статус индексирования у самого TaskManagerEngine
    public IndexingResponse stopIndexing() {
        log.info(TEMPLATE_SERVICE_API_REQUEST_STOP_INDEXING);

        List<Site> existingSites =
                sitesComponent.getExistingSitesFromDBAndMatchWithConfig();

        boolean isAlreadyIndexing = existingSites
                .stream()
                .anyMatch(site ->
                        site.getStatus() == SiteStatusType.INDEXING);

        if (!isAlreadyIndexing) {
            return new IndexingResponse(true, TEMPLATE_API_INDEXING_NOT_STARTED);
        }

        TaskManagerEngine.cancelIndexing();

        return new IndexingResponse(true, null);
    }

    public IndexingResponse indexPage(String url) {
        log.info(TEMPLATE_SERVICE_API_REQUEST_INDEX_PAGE, url);

        if (!sitesComponent.validateUrlByConfig(url)) {
            throw new PageNotRelatedForSiteException(TEMPLATE_API_INDEXING_PAGE_NOT_RELATED_PAGE);
        }

        asyncIndexPageComponent.startAsyncProcessIndexingPage(url);

        return new IndexingResponse(true, null);
    }
}
