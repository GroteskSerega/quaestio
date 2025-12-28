package searchengine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.components.AsyncIndexPageComponent;
import searchengine.components.AsyncIndexingSitesComponent;
import searchengine.components.SitesComponent;
import searchengine.core.engine.TaskManagerEngine;
import searchengine.dto.ResponseBody;
import searchengine.dto.api.IndexingResponse;
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

    private final AsyncIndexingSitesComponent asyncIndexingSitesComponent;
    private final AsyncIndexPageComponent asyncIndexPageComponent;

    @Override
    public ResponseEntity<ResponseBody> startIndexing() {
        log.info(TEMPLATE_SERVICE_API_REQUEST_START_INDEXING);
        List<Site> existingSites =
                sitesComponent.getExistingSitesFromDBAndMatchWithConfig();

        for (Site site : existingSites) {
            if (site.getStatus().equals(SiteStatusType.INDEXING)) {
                return createResponseEntityWithError(TEMPLATE_API_INDEXING_ALREADY_STARTED,
                        HttpStatusCode.valueOf(HTTP_CODE_ACCEPT));
            }
        }

        asyncIndexingSitesComponent.startAsyncProcessIndexingSites(existingSites);

        return createSuccessResponseEntity(HttpStatusCode.valueOf(HTTP_CODE_ACCEPT));
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
            return createResponseEntityWithError(TEMPLATE_API_INDEXING_NOT_STARTED,
                    HttpStatusCode.valueOf(HTTP_CODE_OK));
        }

        TaskManagerEngine.cancel();

        return createSuccessResponseEntity(HttpStatusCode.valueOf(HTTP_CODE_OK));
    }

    @Override
    public ResponseEntity<ResponseBody> indexPage(String url) {
        log.info(TEMPLATE_SERVICE_API_REQUEST_INDEX_PAGE, url);

        boolean urlIsValid = sitesComponent.validateUrlByConfig(url);

        if (!urlIsValid) {
            return createResponseEntityWithError(TEMPLATE_API_INDEXING_PAGE_NOT_RELATED_PAGE,
                    HttpStatusCode.valueOf(HTTP_CODE_BAD_REQUEST));
        }

        asyncIndexPageComponent.startAsyncProcessIndexingPage(url);

        return createSuccessResponseEntity(HttpStatusCode.valueOf(HTTP_CODE_ACCEPT));
    }

    private ResponseEntity<ResponseBody> createResponseEntityWithError(String template,
                                                                       HttpStatusCode httpStatusCode) {
        IndexingResponse errorBody = new IndexingResponse();
        errorBody.setResult(false);
        errorBody.setError(template);
        log.info(TEMPLATE_SERVICE_RESPONSE,
                httpStatusCode,
                errorBody);
        return new ResponseEntity<>(errorBody, httpStatusCode);
    }

    private ResponseEntity<ResponseBody> createSuccessResponseEntity(HttpStatusCode httpStatusCode) {
        IndexingResponse responseBody = new IndexingResponse();
        responseBody.setResult(true);
        log.info(TEMPLATE_SERVICE_RESPONSE,
                responseBody,
                httpStatusCode);
        return new ResponseEntity<>(responseBody, httpStatusCode);
    }
}
