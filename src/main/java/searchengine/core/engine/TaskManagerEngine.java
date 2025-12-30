package searchengine.core.engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import searchengine.components.*;
import searchengine.config.JsoupConfig;
import searchengine.core.utility.JsoupUtility;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;

import static searchengine.logging.LoggingTemplates.TEMPLATE_RECURSIVE_TASK_MANAGER_ENGINE_LINK;

@Slf4j
@Getter
@RequiredArgsConstructor
public class TaskManagerEngine extends RecursiveTask<Set<String>> {

    private final String link;
    private final String vertexLink;
    private final Site site;

    private final SitesComponent sitesComponent;
    private final PagesComponent pagesComponent;
    private final LemmasComponent lemmasComponent;
    private final IndexesComponent indexesComponent;

    private final LuceneMorphologyComponent luceneMorphologyComponent;

    private final JsoupConfig jsoupConfig;

    private static AtomicBoolean cancelled =
            new AtomicBoolean();

    private static final int MIN_WAITING_MILLISECONDS = 100;
    private static final int MAX_WAITING_MILLISECONDS = 200;

    private static final String FIRST_CHAR_LINK_FOR_VALIDATE = "/";
    private static final String REGEX_VALID_LINKS = "^/.+";
    private static final String TEMPLATE_REGEX_VERTEX_LINK = "%s.+";

    private static final Object SYNC_SELECT_INDEXES = new Object();

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

        Page newPage = JsoupUtility.createPageByLink(link, jsoupConfig);
        if (newPage == null) {
            return linkSet;
        }

        String uri = link.substring(site.getUrl().length() - 1);
        newPage.setPath(uri);
        newPage.setSite(site);

        Set<String> childrenLinks =
                JsoupUtility.getLinksFromContent(newPage.getContent());

        if (childrenLinks.isEmpty()) {
            return linkSet;
        }

        Page checkExistsPage = pagesComponent.findFirstPageBySiteIdAndPathInDB(newPage.getSite().getId(),
                newPage.getPath());

        if (checkExistsPage != null) {
            return linkSet;
        }

        Page savedPage = pagesComponent.selectOrInsertPageToDB(newPage);
        site.setStatusTime(LocalDateTime.now());
        sitesComponent.saveSiteToDB(site);

        processingLemmasAndIndexes(savedPage);

        Set<String> filteredLinks =
                filteringLinksByHostAndCreateFullLink(childrenLinks,
                        vertexLink);

        removeLinksThatAlreadySaved(filteredLinks);

        List<TaskManagerEngine> taskList = createNewTasks(filteredLinks);

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

    private void removeLinksThatAlreadySaved(Set<String> filteredLinks) {
        Iterator<String> linkIterator = filteredLinks.iterator();

        while (linkIterator.hasNext()) {
            String newLink = linkIterator.next();

            String candidateUri = newLink.substring(site.getUrl().length() - 1);
            Page pageCandidate = pagesComponent.findFirstPageBySiteIdAndPathInDB(site.getId(), candidateUri);

            if (pageCandidate != null) {
                linkIterator.remove();
            }
        }
    }

    private List<TaskManagerEngine> createNewTasks(Set<String> filteredLinks) {
        List<TaskManagerEngine> taskList = new ArrayList<>();

        for (String link : filteredLinks) {
            TaskManagerEngine task = new TaskManagerEngine(link,
                    vertexLink,
                    site,
                    sitesComponent,
                    pagesComponent,
                    lemmasComponent,
                    indexesComponent,
                    luceneMorphologyComponent,
                    jsoupConfig);
            task.fork();
            taskList.add(task);
        }
        return taskList;
    }

    private static Set<String> filteringLinksByHostAndCreateFullLink(Set<String> links,
                                                                    String vertexLink) {
        Set<String> filteredLinks = new HashSet<>();

        String regexCheckLinkByVertexLink =
                String.format(TEMPLATE_REGEX_VERTEX_LINK, vertexLink);

        for (String link : links) {
            boolean isValidLink = link.matches(REGEX_VALID_LINKS);
            boolean isLinkWithVertexLink =
                    link.matches(regexCheckLinkByVertexLink);

            if (isLinkWithVertexLink) {
                filteredLinks.add(link);
            }

            if (isValidLink) {
                if (link.startsWith(FIRST_CHAR_LINK_FOR_VALIDATE)) {
                    link = vertexLink.concat(link.substring(1));
                }
                filteredLinks.add(link);
            }
        }
        return filteredLinks;
    }

    private void processingLemmasAndIndexes(Page savedPage) {
        String text =
                luceneMorphologyComponent.getTextFromHTML(savedPage.getContent());

        for (LuceneMorphologyComponent.Lang lang : LuceneMorphologyComponent.Lang.values()) {
            Map<String, Integer> mapOfLemmas =
                    luceneMorphologyComponent.calculateLemmas(text,
                            lang);
            // savedPage can be exist
            Iterable<Lemma> lemmasIterable = lemmasComponent.prepareAndSaveLemmas(mapOfLemmas, savedPage);
            indexesComponent.prepareAndSaveIndexes(mapOfLemmas, lemmasIterable, savedPage);
        }
    }
}
