package searchengine.core.engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import searchengine.components.PagesComponent;
import searchengine.components.SitesComponent;
import searchengine.config.JsoupConfig;
import searchengine.core.utility.JsoupUtility;
import searchengine.model.Page;
import searchengine.model.Site;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static searchengine.logging.LoggingTemplates.TEMPLATE_RECURSIVE_TASK_MANAGER_ENGINE_LINK;

// TODO SOME SPAGHETTI CODE WITH JsoupUtility class. NEED REFACTORING
@Slf4j
@Getter
@RequiredArgsConstructor
public class TaskManagerEngine extends RecursiveTask<Set<String>> {

    private final String link;
    private final String vertexLink;
    private final Site site;

    private final SitesComponent sitesComponent;
    private final PagesComponent pagesComponent;

    private final JsoupConfig jsoupConfig;

    private static final AtomicBoolean cancelled =
            new AtomicBoolean();


    private static final int MIN_WAITING_MILLISECONDS = 100;
    private static final int MAX_WAITING_MILLISECONDS = 1500;

    private static final Object SYNC = new Object();

    public static void setRunning() {
        cancelled.set(false);
    }

    public static boolean isCancel() {
        return cancelled.get();
    }

    public static void cancel() {
        cancelled.set(true);
    }

    @Override
    protected Set<String> compute() {
        try {
            Thread.sleep(generateTimeForWaiting());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }

        Set<String> linkSet = new HashSet<>();

        if (cancelled.get()) {
            return linkSet;
        }

        log.info(TEMPLATE_RECURSIVE_TASK_MANAGER_ENGINE_LINK, link);

        Page newPage = createNewPage(link);

        Set<String> childrenLinks = JsoupUtility.getChildrenPageLinks(link,
                newPage,
                jsoupConfig);

        if (childrenLinks.isEmpty()) {
            return linkSet;
        }

        // TODO change current logic to PESSIMISTIC_WRITE OR SOMETHING
        // CURRENT LOGIC:
        // -:
        // NOT EFFECTIVE FOR SEVERAL SITES
        // NOT WORKED FOR SEVERAL INSTANCE OF THESE APP
        // +:
        // NOT CREATE DUPLICATE OF PAGES IN DATABASE
        synchronized (SYNC) {
            pagesComponent.readAndInsertPage(site.getId(), newPage);
            site.setStatusTime(LocalDateTime.now());
            sitesComponent.saveSiteToDB(site);
        }

//        log.info("\nLink: {} \nChildren links: \n{}",
//                link,
//                Arrays.toString(childrenLinks.toArray()));

        Set<String> filteredLinks =
                JsoupUtility.filteringLinksByHostAndCreateFullLink(childrenLinks,
                        vertexLink);

//        log.info("\nLink: {} \nFiltered links: \n{}",
//                link,
//                Arrays.toString(filteredLinks.toArray()));

        removeLinksNotRelatedToSite(filteredLinks);

        List<TaskManagerEngine> taskList = new ArrayList<>();

        for (String link : filteredLinks) {
            TaskManagerEngine task = new TaskManagerEngine(link,
                    vertexLink,
                    site,
                    sitesComponent,
                    pagesComponent,
                    jsoupConfig);
            task.fork();
            taskList.add(task);
        }

        for (TaskManagerEngine task : taskList) {
            linkSet.addAll(task.join());
        }

        linkSet.addAll(filteredLinks);

        return linkSet;
    }

    private static long generateTimeForWaiting() {
        return Math.round((Math.random()) *
                (MAX_WAITING_MILLISECONDS - MIN_WAITING_MILLISECONDS)) +
                MIN_WAITING_MILLISECONDS;
    }

    private Page createNewPage(String link) {
        String uri = link.substring(site.getUrl().length() - 1);

        Page newPage = new Page();
        newPage.setSite(site);
        newPage.setPath(uri);
        return newPage;
    }

    private void removeLinksNotRelatedToSite(Set<String> filteredLinks) {
        Iterator<String> linkIterator = filteredLinks.iterator();

        while (linkIterator.hasNext()) {
            String newLink = linkIterator.next();

            String candidateUri = newLink.substring(site.getUrl().length() - 1);
            Page pageCandidate = pagesComponent.findFirstPageBySiteIdAndPath(site.getId(), candidateUri);

            if (pageCandidate != null) {
                linkIterator.remove();
            }
        }
    }
}
