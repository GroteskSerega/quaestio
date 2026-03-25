package searchengine.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import searchengine.core.engine.TaskManagerEngine;
import searchengine.entity.Site;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AsyncIndexingSitesComponentImpl implements AsyncIndexingSitesComponent {

    private final SitesComponent sitesComponent;
    private final PagesComponent pagesComponent;
    private final LemmasComponent lemmasComponent;
    private final IndexesComponent indexesComponent;

    private final ForkJoinPoolComponent forkJoinPoolComponent;

    @Async
    @Transactional
    public void startAsyncProcessIndexingSites(List<Site> existingSites) {
        List<Integer> siteIds = existingSites
                .stream()
                .map(Site::getId)
                .toList();

        indexesComponent.deleteBySiteIds(siteIds);
        lemmasComponent.deleteBySiteIds(siteIds);
        pagesComponent.deleteBySiteIds(siteIds);
        sitesComponent.deleteByIds(siteIds);

        List<Site> sitesEntities = sitesComponent.createSitesFromConfigForIndexing();
        List<Site> savedSites = sitesComponent.saveSitesToDB(sitesEntities);

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        TaskManagerEngine.setRunningIndexing();

                        savedSites.forEach(forkJoinPoolComponent::startIndexSite);
                    }
                });
    }
}