package searchengine.core.engine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import searchengine.component.*;
import searchengine.component.core.engine.JsoupComponent;
import searchengine.entity.Lemma;
import searchengine.entity.Page;
import searchengine.entity.Site;
import searchengine.exception.PageAlreadyExists;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;

import static searchengine.core.engine.EngineLoggingTemplates.*;

@Slf4j
@Getter
@RequiredArgsConstructor
public class TaskManagerEngine extends RecursiveAction {

    private final String link;
    private final String vertexLink;
    private final Site site;

    private final SitesComponent sitesComponent;
    private final PagesComponent pagesComponent;
    private final LemmasComponent lemmasComponent;
    private final IndexesComponent indexesComponent;

    private final LuceneMorphologyComponent luceneMorphologyComponent;

    private final JsoupComponent jsoupComponent;

    private static AtomicBoolean cancelled =
            new AtomicBoolean();

    private static final int MIN_WAITING_MILLISECONDS = 100;
    private static final int MAX_WAITING_MILLISECONDS = 200;

    private static final String FIRST_CHAR_LINK_FOR_VALIDATE = "/";
    private static final String REGEX_VALID_LINKS = "^/.+";
    private static final String TEMPLATE_REGEX_VERTEX_LINK = "%s.+";

    public static void setRunningIndexing() {
        cancelled.set(false);
    }

    public static boolean isCancelIndexing() {
        return cancelled.get();
    }

    public static void cancelIndexing() {
        cancelled.set(true);
    }

    public TaskManagerEngine(DtoTaskManager dtoTaskManager) {
        this.link = dtoTaskManager.link();
        this.vertexLink = dtoTaskManager.vertexLink();
        this.site = dtoTaskManager.site();

        this.sitesComponent = dtoTaskManager.sitesComponent();
        this.pagesComponent = dtoTaskManager.pagesComponent();
        this.lemmasComponent = dtoTaskManager.lemmasComponent();
        this.indexesComponent = dtoTaskManager.indexesComponent();

        this.luceneMorphologyComponent = dtoTaskManager.luceneMorphologyComponent();
        this.jsoupComponent = dtoTaskManager.jsoupComponent();
    }

    @Override
    protected void compute() {

        if (cancelled.get()) {
            return;
        }

        log.info(TEMPLATE_RECURSIVE_TASK_MANAGER_ENGINE_LINK, link);

        String uri = link.substring(site.getUrl().length() - 1);

        Optional<Connection.Response> optionalResponse = jsoupComponent.getResponse(link);

        if (optionalResponse.isEmpty()) {
            return;
        }

        Optional<Document> optionalDocument = jsoupComponent.getDocument(link, optionalResponse.get());

        Page newPage = new Page();

        newPage.setCode(optionalResponse.get().statusCode());

        newPage.setPath(uri);
        newPage.setSite(site);

        optionalDocument.ifPresent(document -> {
                newPage.setContent(document.outerHtml());
//                newPage.setTitle(jsoupComponent.getTitleFromContent(document));
        });

        Set<String> childrenLinks;

        // TODO VALIDATE
        childrenLinks = optionalDocument.map(jsoupComponent::getLinksFromDocument)
                .orElse(Set.of());

        log.info(TEMPLATE_TASK_MANAGER_COUNT_LINKS, childrenLinks.size());

        if (childrenLinks.isEmpty()) {
            return;
        }

        Page existedPage;

        try {
            existedPage = pagesComponent.selectOrInsertPage(newPage);
        } catch (PageAlreadyExists e) {
            return;
        }

        site.setStatusTime(LocalDateTime.now());
        sitesComponent.saveSiteToDB(site);

        try {
            processingLemmasAndIndexes(existedPage, optionalDocument.get());
        } catch (Exception e) {
            log.error("Failed to process lemmas for {}, error: {}", link, e.getMessage());
        }

        Set<String> filteredLinks =
                filteringLinksByHostAndCreateFullLink(childrenLinks,
                        vertexLink);

        removeLinksThatAlreadySaved(filteredLinks);

        log.info(TEMPLATE_TASK_MANAGER_COUNT_LINKS_AND_FILTERED_LINKS,
                childrenLinks.size(),
                filteredLinks.size());

        try {
            Thread.sleep(generateTimeForWaiting());
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new RuntimeException();
        }

        createNewTasks(filteredLinks);
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

    private void createNewTasks(Set<String> filteredLinks) {
        List<TaskManagerEngine> tasks = new ArrayList<>();

        log.info(TEMPLATE_TASK_MANAGER_FOUNDED_NEW_FILTERED_LINKS,
                String.join(",", filteredLinks));

        for (String link : filteredLinks) {

            DtoTaskManager dtoTaskManager =
                    new DtoTaskManager(link,
                            vertexLink,
                            site,
                            sitesComponent,
                            pagesComponent,
                            lemmasComponent,
                            indexesComponent,
                            luceneMorphologyComponent,
                            jsoupComponent);

            tasks.add(new TaskManagerEngine(dtoTaskManager));

        }

        if (!tasks.isEmpty()) {
            invokeAll(tasks);
        }
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

    private void processingLemmasAndIndexes(Page savedPage, Document document) {
        String text =
                jsoupComponent.getTextFromDocument(document);

        Map<String, Integer> mapOfLemmas =
                luceneMorphologyComponent.calculateLemmas(text);

        // savedPage can be exist
        List<Lemma> lemmaList =
                lemmasComponent.prepareAndSaveLemmas(mapOfLemmas, savedPage);

        indexesComponent.prepareAndSaveIndexes(mapOfLemmas,
                lemmaList,
                savedPage);
    }


}
