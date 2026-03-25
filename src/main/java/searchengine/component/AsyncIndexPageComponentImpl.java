package searchengine.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.component.core.engine.JsoupComponent;
import searchengine.entity.*;
import searchengine.exception.NotFoundSiteException;
import searchengine.exception.SiteStatusIncorrect;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static searchengine.service.ServiceLoggingTemplates.*;
import static searchengine.web.dto.MessagesTemplates.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class AsyncIndexPageComponentImpl implements AsyncIndexPageComponent {

    private final SitesComponent sitesComponent;
    private final PagesComponent pagesComponent;
    private final LemmasComponent lemmasComponent;
    private final IndexesComponent indexesComponent;

    private final LuceneMorphologyComponent luceneMorphologyComponent;

    private final JsoupComponent jsoupComponent;

    @Async
    public void startAsyncProcessIndexingPage(String url) {
        log.info(TEMPLATE_ENGINE_STARTED_PROCESSING_URL,
                url);
        Site site = sitesComponent.getSiteMatchUrl(url);
        if (site == null) {
            throw new NotFoundSiteException(TEMPLATE_API_INDEXING_PAGE_NOT_RELATED_PAGE);
        }

        if (site.getStatus() == SiteStatusType.INDEXING) {
            throw new SiteStatusIncorrect(TEMPLATE_API_PAGE_SITE_STATUS_INCORRECT);
        }

        site.setStatusTime(LocalDateTime.now());
        site.setLastError(null);
        site.setStatus(SiteStatusType.INDEXING);
        sitesComponent.saveSiteToDB(site);

        String uri = url.substring(site.getUrl().length() - 1);
        clearDataInDB(site, uri);

        Optional<Connection.Response> optionalResponse = jsoupComponent.getResponse(url);

        if (optionalResponse.isEmpty()) {
            saveFailForSiteToDB(site);
            return;
        }

        Optional<Document> optionalDocument = jsoupComponent.getDocument(url, optionalResponse.get());

        Page newPage = new Page();

        newPage.setCode(optionalResponse.get().statusCode());

        newPage.setSite(site);
        newPage.setPath(uri);

        optionalDocument.ifPresent(document -> {
                newPage.setContent(document.outerHtml());
//                newPage.setTitle(jsoupComponent.getTitleFromContent(document));
        });

        Page savedPage = pagesComponent.savePageToDB(newPage);

        // TODO VALIDATE
        String text = optionalDocument.map(jsoupComponent::getTextFromDocument)
                .orElse("");

//        if (text.isBlank()) {
//
//            return;
//        }

        Map<String, Integer> mapOfLemmas =
                luceneMorphologyComponent.calculateLemmas(text);

        List<Lemma> lemmaList =
                lemmasComponent.prepareAndSaveLemmas(mapOfLemmas, savedPage);

        indexesComponent.prepareAndSaveIndexes(mapOfLemmas, lemmaList, savedPage);

        site.setStatus(SiteStatusType.INDEXED);
        site.setStatusTime(LocalDateTime.now());
        sitesComponent.saveSiteToDB(site);
        log.info(TEMPLATE_ENGINE_FINISHED_PROCESSING_URL,
                url);
    }

    @Transactional
    private void clearDataInDB(Site site, String uri) {
        Page foundPage = pagesComponent.findFirstPageBySiteIdAndPathInDB(site.getId(), uri);
        if (foundPage != null) {
            List<Index> indexes = indexesComponent.findAllByPageId(foundPage.getId());
            correctLemmasDueToIndexesDeletion(indexes);
            indexesComponent.deleteIndexesByPageIdInDB(foundPage.getId());
            pagesComponent.deletePageBySiteIdAndPathInDB(foundPage.getSite().getId(), uri);
        }
    }

    private void correctLemmasDueToIndexesDeletion(List<Index> indexes) {
        List<Lemma> lemmaForUpdate = new ArrayList<>();
        for (Index index : indexes) {
            Lemma lemma = index.getLemma();
            lemma.setFrequency(lemma.getFrequency() - 1);
            lemmaForUpdate.add(lemma);

            if (lemma.getFrequency() <= 0) {
                lemmasComponent.deleteById(lemma.getId());
                return;
            }
        }

        lemmasComponent.saveLemmasToDB(lemmaForUpdate);
    }

    private void saveFailForSiteToDB(Site site) {
        site.setStatus(SiteStatusType.FAILED);
        site.setLastError(TEMPLATE_REASON_FAILED_SITE_CAN_NOT_BE_REACHED);
        sitesComponent.saveSiteToDB(site);
    }
}
