package searchengine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.components.AsyncIndexingComponent;
import searchengine.components.SitesComponent;
import searchengine.core.engine.TaskManagerEngine;
import searchengine.dto.ResponseBody;
import searchengine.dto.api.IndexingResponse;
import searchengine.model.Site;
import searchengine.model.SiteStatusType;

import java.util.*;

import static searchengine.logging.LoggingTemplates.*;
import static searchengine.messages.MessagesTemplates.*;


@Slf4j
@RequiredArgsConstructor
@Service
public class ApisServiceImpl implements ApisService<ResponseBody> {

    private final SitesComponent sitesComponent;

    private final AsyncIndexingComponent asyncIndexingComponent;

    private static final int HTTP_CODE_ACCEPT = 202;
    private static final int HTTP_CODE_OK = 200;

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

        asyncIndexingComponent.startAsyncProcessIndexingSites(existingSites);

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
