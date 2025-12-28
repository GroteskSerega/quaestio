package searchengine.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import searchengine.core.engine.TaskManagerEngine;
import searchengine.model.Site;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AsyncIndexingSitesComponentImp implements AsyncIndexingSitesComponent {

    private final SitesComponent sitesComponent;
    private final PagesComponent pagesComponent;
    private final LemmasComponent lemmasComponent;
    private final IndexesComponent indexesComponent;

    private final ForkJoinPoolComponent forkJoinPoolComponent;

    @Async
    @Override
    public void startAsyncProcessIndexingSites(List<Site> existingSites) {
        for (Site site : existingSites) {
            List<Integer> ids = pagesComponent.findAllIdsBySiteId(site.getId());
            indexesComponent.deleteAllByPageIdIn(ids);
        }
        lemmasComponent.deleteLemmasInDB(existingSites);
        pagesComponent.deletePagesInDB(existingSites);
        sitesComponent.deleteSitesInDB(existingSites);

        List<Site> sitesEntities = sitesComponent.createSitesFromConfigForIndexing();
        Iterable<Site> savedSites = sitesComponent.saveSitesToDB(sitesEntities);

        TaskManagerEngine.setRunning();

        for (Site site : savedSites) {
            forkJoinPoolComponent.startIndexSite(site);
        }
    }
}