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

import static searchengine.service.LoggingTemplates.*;
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

        for (Site site : existingSites) {
            if (site.getStatus().equals(SiteStatusType.INDEXING)) {
                return new IndexingResponse(true, TEMPLATE_API_INDEXING_ALREADY_STARTED);
            }
        }

        asyncIndexingSitesComponent.startAsyncProcessIndexingSites(existingSites);

        return new IndexingResponse(true, null);
    }

    public IndexingResponse stopIndexing() {
        log.info(TEMPLATE_SERVICE_API_REQUEST_STOP_INDEXING);

        List<Site> existingSites =
                sitesComponent.getExistingSitesFromDBAndMatchWithConfig();

        boolean indexingStarted = false;

        for (Site site : existingSites) {
            if (site.getStatus().equals(SiteStatusType.INDEXING)) {
                indexingStarted = true;
                break;
            }
        }

        if (!indexingStarted) {
            return new IndexingResponse(true, TEMPLATE_API_INDEXING_NOT_STARTED);
        }

        TaskManagerEngine.cancel();

        return new IndexingResponse(true, null);
    }

    public IndexingResponse indexPage(String url) {
        log.info(TEMPLATE_SERVICE_API_REQUEST_INDEX_PAGE, url);

        boolean urlIsValid = sitesComponent.validateUrlByConfig(url);

        if (!urlIsValid) {
            throw new PageNotRelatedForSiteException(TEMPLATE_API_INDEXING_PAGE_NOT_RELATED_PAGE);
        }

        asyncIndexPageComponent.startAsyncProcessIndexingPage(url);

        return new IndexingResponse(true, null);
    }
}
