package searchengine.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import searchengine.config.JsoupConfig;
import searchengine.core.utility.JsoupUtility;
import searchengine.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static searchengine.logging.LoggingTemplates.TEMPLATE_ENGINE_FINISHED_PROCESSING_URL;
import static searchengine.logging.LoggingTemplates.TEMPLATE_ENGINE_STARTED_PROCESSING_URL;
import static searchengine.messages.MessagesTemplates.TEMPLATE_REASON_FAILED_SITE_CAN_NOT_BE_REACHED;

@Slf4j
@RequiredArgsConstructor
@Component
public class AsyncIndexPageComponentImpl implements AsyncIndexPageComponent {

    private final SitesComponent sitesComponent;
    private final PagesComponent pagesComponent;
    private final LemmasComponent lemmasComponent;
    private final IndexesComponent indexesComponent;

    private final LuceneMorphologyComponent luceneMorphologyComponent;

    private final JsoupConfig jsoupConfig;

    @Async
    @Override
    public void startAsyncProcessIndexingPage(String url) {
        log.info(TEMPLATE_ENGINE_STARTED_PROCESSING_URL,
                url);
        Site site = sitesComponent.getSiteMatchUrl(url);
        if (site == null) {
            return;
        }
        site.setStatusTime(LocalDateTime.now());
        site.setLastError(null);
        site.setStatus(SiteStatusType.INDEXING);
        sitesComponent.saveSiteToDB(site);

        String uri = url.substring(site.getUrl().length() - 1);
        clearDataInDB(site, uri);

        Page newPage = JsoupUtility.createPageByLink(url, jsoupConfig);
        if (newPage == null) {
            saveFailForSiteToDB(site);
            return;
        }

        newPage.setSite(site);
        newPage.setPath(uri);
        Page savedPage = pagesComponent.savePageToDB(newPage);

        String text =
                luceneMorphologyComponent.getTextFromHTML(savedPage.getContent());

        for (LuceneMorphologyComponent.Lang lang: LuceneMorphologyComponent.Lang.values()) {
            Map<String, Integer> mapOfLemmas =
                    luceneMorphologyComponent.calculateLemmas(text,
                            lang);
            Iterable<Lemma> lemmasIterable = lemmasComponent.prepareAndSaveLemmas(mapOfLemmas, savedPage);
            indexesComponent.prepareAndSaveIndexes(mapOfLemmas, lemmasIterable, savedPage);
        }

        site.setStatus(SiteStatusType.INDEXED);
        site.setStatusTime(LocalDateTime.now());
        sitesComponent.saveSiteToDB(site);
        log.info(TEMPLATE_ENGINE_FINISHED_PROCESSING_URL,
                url);
    }

    private void clearDataInDB(Site site, String uri) {
        Page foundPage = pagesComponent.findFirstPageBySiteIdAndPathInDB(site.getId(), uri);
        if (foundPage != null) {
            Iterable<Index> indexIterable = indexesComponent.findAllByPageId(foundPage.getId());
            correctLemmasDueToIndexesDeletion(indexIterable);
            indexesComponent.deleteIndexesByPageIdInDB(foundPage.getId());
            pagesComponent.deletePageBySiteIdAndPathInDB(foundPage.getSite().getId(), uri);
        }
    }

    private void correctLemmasDueToIndexesDeletion(Iterable<Index> indexIterable) {
        List<Lemma> lemmaForUpdate = new ArrayList<>();
        for (Index index : indexIterable) {
            Lemma lemma = index.getLemma();
            lemma.setFrequency(lemma.getFrequency() - 1);
            lemmaForUpdate.add(lemma);
        }
        lemmasComponent.saveLemmasToDB(lemmaForUpdate);
    }

    private void saveFailForSiteToDB(Site site) {
        site.setStatus(SiteStatusType.FAILED);
        site.setLastError(TEMPLATE_REASON_FAILED_SITE_CAN_NOT_BE_REACHED);
        sitesComponent.saveSiteToDB(site);
    }
}
