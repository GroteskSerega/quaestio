package searchengine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.components.AsyncIndexPageComponent;
import searchengine.components.AsyncIndexingSitesComponent;
import searchengine.components.ResponseEntityComponent;
import searchengine.components.SitesComponent;
import searchengine.core.engine.TaskManagerEngine;
import searchengine.dto.ResponseBody;
import searchengine.model.Site;
import searchengine.model.SiteStatusType;

import java.util.*;

import static searchengine.httpstatuscodes.HttpStatusCodes.*;
import static searchengine.logging.LoggingTemplates.*;
import static searchengine.messages.MessagesTemplates.*;


@Slf4j
@RequiredArgsConstructor
@Service
public class ApisServiceImpl implements ApisService<ResponseBody> {

    private final SitesComponent sitesComponent;
    private final ResponseEntityComponent responseEntityComponent;

    private final AsyncIndexingSitesComponent asyncIndexingSitesComponent;
    private final AsyncIndexPageComponent asyncIndexPageComponent;

    @Override
    public ResponseEntity<ResponseBody> startIndexing() {
        log.info(TEMPLATE_SERVICE_API_REQUEST_START_INDEXING);
        List<Site> existingSites =
                sitesComponent.getExistingSitesFromDBAndMatchWithConfig();

        for (Site site : existingSites) {
            if (site.getStatus().equals(SiteStatusType.INDEXING)) {
                return responseEntityComponent.createResponseEntity(TEMPLATE_API_INDEXING_ALREADY_STARTED,
                        false,
                        HttpStatusCode.valueOf(HTTP_CODE_ACCEPT));
            }
        }

        asyncIndexingSitesComponent.startAsyncProcessIndexingSites(existingSites);

        return responseEntityComponent.createResponseEntity(null,
                true,
                HttpStatusCode.valueOf(HTTP_CODE_ACCEPT));
    }

    @Override
    public ResponseEntity<ResponseBody> stopIndexing() {
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
            return responseEntityComponent.createResponseEntity(TEMPLATE_API_INDEXING_NOT_STARTED,
                    false,
                    HttpStatusCode.valueOf(HTTP_CODE_OK));
        }

        TaskManagerEngine.cancel();

        return responseEntityComponent.createResponseEntity(null,
                true,
                HttpStatusCode.valueOf(HTTP_CODE_OK));
    }

    @Override
    public ResponseEntity<ResponseBody> indexPage(String url) {
        log.info(TEMPLATE_SERVICE_API_REQUEST_INDEX_PAGE, url);

        boolean urlIsValid = sitesComponent.validateUrlByConfig(url);

        if (!urlIsValid) {
            return responseEntityComponent.createResponseEntity(TEMPLATE_API_INDEXING_PAGE_NOT_RELATED_PAGE,
                    false,
                    HttpStatusCode.valueOf(HTTP_CODE_BAD_REQUEST));
        }

        asyncIndexPageComponent.startAsyncProcessIndexingPage(url);

        return responseEntityComponent.createResponseEntity(null,
                true,
                HttpStatusCode.valueOf(HTTP_CODE_ACCEPT));
    }
}
