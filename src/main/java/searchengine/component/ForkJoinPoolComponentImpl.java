package searchengine.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import searchengine.component.core.engine.JsoupComponent;
import searchengine.core.engine.DtoTaskManager;
import searchengine.core.engine.TaskManagerEngine;
import searchengine.entity.Page;
import searchengine.entity.Site;
import searchengine.entity.SiteStatusType;

import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static searchengine.component.ComponentLoggingTemplates.*;
import static searchengine.service.ServiceLoggingTemplates.*;
import static searchengine.web.dto.MessagesTemplates.TEMPLATE_REASON_FAILED_BY_USER;
import static searchengine.web.dto.MessagesTemplates.TEMPLATE_REASON_FAILED_SITE_CAN_NOT_BE_REACHED;

@Slf4j
@RequiredArgsConstructor
@Component
public class ForkJoinPoolComponentImpl implements ForkJoinPoolComponent {

    private final ForkJoinPool indexingPoolConfig;

    private final SitesComponent sitesComponent;
    private final PagesComponent pagesComponent;
    private final LemmasComponent lemmasComponent;
    private final IndexesComponent indexesComponent;

    private final LuceneMorphologyComponent luceneMorphologyComponent;

    private final JsoupComponent jsoupComponent;

    @Value("${app.secondsWaitForGetStatisticsPool}")
    private int secondsWaitForGetStatisticsPool;

    @Async
    public void startIndexSite(Site site) {
        TaskManagerEngine taskManager = prepareTasksManager(site);

        startProcessIndexing(taskManager);

        scanningStatusOfIndexing(taskManager);

        saveResultsOfIndexing(taskManager);

        log.info(TEMPLATE_FORK_JOIN_POOL_COMPLETED,
                indexingPoolConfig);

        log.info(TEMPLATE_ENGINE_FINISHED_PROCESSING_SITE, site.getName());
    }

    private TaskManagerEngine prepareTasksManager(Site site) {
        DtoTaskManager dtoTaskManager =
                new DtoTaskManager(site.getUrl(),
                        site.getUrl(),
                        site,
                        sitesComponent,
                        pagesComponent,
                        lemmasComponent,
                        indexesComponent,
                        luceneMorphologyComponent,
                        jsoupComponent);

        return new TaskManagerEngine(dtoTaskManager);
    }

    private void startProcessIndexing(TaskManagerEngine taskManager) {
        log.info(TEMPLATE_FORK_JOIN_POOL_USING,
                indexingPoolConfig);
        log.info(TEMPLATE_ENGINE_STARTED_PROCESSING_SITE,
                taskManager.getSite().getName());
        indexingPoolConfig.execute(taskManager);
    }

    private void scanningStatusOfIndexing(TaskManagerEngine taskManager) {
        while (!taskManager.isDone() && !TaskManagerEngine.isCancelIndexing()) {
            try {
                TimeUnit.SECONDS.sleep(secondsWaitForGetStatisticsPool);
                log.info(TEMPLATE_FORK_JOIN_POOL_GET_STATISTIC,
                        taskManager.getSite().getName(),
                        taskManager.isDone(),
                        taskManager.isCancelled(),
                        taskManager.isCompletedAbnormally(),
                        taskManager.isCompletedNormally(),
                        TaskManagerEngine.isCancelIndexing(),
                        indexingPoolConfig.getParallelism(),
                        indexingPoolConfig.getActiveThreadCount(),
                        indexingPoolConfig.getQueuedTaskCount(),
                        indexingPoolConfig.getStealCount());
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }

            if (TaskManagerEngine.isCancelIndexing() && !taskManager.isDone()) {
                taskManager.cancel(true);
            }
        }
    }

    private void saveResultsOfIndexing(TaskManagerEngine taskManager) {
        Site site = taskManager.getSite();
        Page anyPage = pagesComponent.findFirstPageBySiteIdInDB(site.getId());
        if (anyPage != null) {
            if (TaskManagerEngine.isCancelIndexing()) {
                site.setStatus(SiteStatusType.FAILED);
                site.setLastError(TEMPLATE_REASON_FAILED_BY_USER);
            } else {
                site.setStatus(SiteStatusType.INDEXED);
            }
        } else {
            site.setStatus(SiteStatusType.FAILED);
            site.setLastError(TEMPLATE_REASON_FAILED_SITE_CAN_NOT_BE_REACHED);
        }
        site.setStatusTime(LocalDateTime.now());
        sitesComponent.saveSiteToDB(site);
    }
}
