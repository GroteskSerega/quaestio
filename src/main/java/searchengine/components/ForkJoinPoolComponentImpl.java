package searchengine.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import searchengine.config.AppConfig;
import searchengine.config.JsoupConfig;
import searchengine.core.engine.TaskManagerEngine;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.model.SiteStatusType;

import java.time.LocalDateTime;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static searchengine.logging.LoggingTemplates.*;
import static searchengine.messages.MessagesTemplates.TEMPLATE_REASON_FAILED_BY_USER;
import static searchengine.messages.MessagesTemplates.TEMPLATE_REASON_FAILED_SITE_CAN_NOT_BE_REACHED;

@Slf4j
@RequiredArgsConstructor
@Component
public class ForkJoinPoolComponentImpl implements ForkJoinPoolComponent {

    private final SitesComponent sitesComponent;
    private final PagesComponent pagesComponent;

    private final AppConfig appConfig;
    private final JsoupConfig jsoupConfig;

    private static final int SECONDS_WAIT_FOR_GET_STATISTICS_POOL = 5;

    @Async
    @Override
    public void startIndexSite(Site site) {
        TaskManagerEngine taskManager = prepareTasksManager(site);
        ForkJoinPool forkJoinPool = startProcessIndexing(taskManager);
        scanningStatusOfIndexing(forkJoinPool, taskManager);
        saveResultsOfIndexing(taskManager);
        log.info(TEMPLATE_CONFIG_FORK_JOIN_POOL_SHUTDOWN,
                forkJoinPool);
        forkJoinPool.shutdown();
    }

    private TaskManagerEngine prepareTasksManager(Site site) {
        return new TaskManagerEngine(site.getUrl(),
                site.getUrl(),
                site,
                sitesComponent,
                pagesComponent,
                jsoupConfig);
    }

    private ForkJoinPool startProcessIndexing(TaskManagerEngine taskManager) {
        ForkJoinPool forkJoinPool = new ForkJoinPool(appConfig.getThreadCount());
        log.info(TEMPLATE_CONFIG_FORK_JOIN_POOL_CREATE,
                forkJoinPool);
        log.info(TEMPLATE_ENGINE_STARTED_PROCESSING_SITE,
                taskManager.getSite().getUrl());
        forkJoinPool.execute(taskManager);
        return forkJoinPool;
    }

    private void scanningStatusOfIndexing(ForkJoinPool forkJoinPool,
                                          TaskManagerEngine taskManager) {
        while (!taskManager.isDone()) {
            try {
                TimeUnit.SECONDS.sleep(SECONDS_WAIT_FOR_GET_STATISTICS_POOL);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
            log.info(TEMPLATE_COMPONENT_FORK_JOIN_POOL_GET_STATISTIC,
                    taskManager.getSite().getName(),
                    forkJoinPool.getParallelism(),
                    forkJoinPool.getActiveThreadCount(),
                    forkJoinPool.getQueuedTaskCount(),
                    forkJoinPool.getStealCount());
            if (TaskManagerEngine.isCancel()) {
                break;
            }
        }
    }

    private void saveResultsOfIndexing(TaskManagerEngine taskManager) {
        //CODE FOR DEBUGGING
//        Set<String> rawLinks = new HashSet<>();
//        rawLinks.addAll(taskManager.join());

//        log.info("result: {}", Arrays.toString(rawLinks.toArray()));
//        log.info("result count for site {}: {}",
//                taskManager.getSite().getName(),
//                rawLinks.size());

        Site site = taskManager.getSite();
        Page anyPage = pagesComponent.findFirstPageBySiteId(site.getId());
        if (anyPage != null) {
            if (TaskManagerEngine.isCancel()) {
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
