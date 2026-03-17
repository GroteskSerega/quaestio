package searchengine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.component.*;
import searchengine.config.SearchConfig;
import searchengine.core.utility.JsoupUtility;
import searchengine.exception.EmptyQuerySearchException;
import searchengine.exception.IncorrectQuerySearchException;
import searchengine.exception.NotFoundIndexedSiteException;
import searchengine.web.dto.api.search.DataPage;
import searchengine.web.dto.api.search.ResponseSearchContainer;
import searchengine.entity.*;

import java.util.*;
import java.util.stream.Collectors;

import static searchengine.service.LoggingTemplates.*;
import static searchengine.web.dto.MessagesTemplates.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchServiceImpl implements SearchService {

    private final SitesComponent sitesComponent;
    private final PagesComponent pagesComponent;
    private final LemmasComponent lemmasComponent;
    private final IndexesComponent indexesComponent;

    private final LuceneMorphologyComponent luceneMorphologyComponent;

    private final SearchConfig searchConfig;

    public ResponseSearchContainer search(String query,
                                                   Integer offset,
                                                   Integer limit,
                                                   String selectedSite) {
        log.info(TEMPLATE_SERVICE_API_REQUEST_SEARCH,
                query,
                offset,
                limit,
                selectedSite);
        if (query == null || query.isEmpty()) {
            throw new EmptyQuerySearchException(TEMPLATE_API_SEARCH_EMPTY_QUERY);
        }

        List<Site> existingSites =
                sitesComponent.getExistingSitesFromDBAndMatchWithConfig();

        if (selectedSite != null && !selectedSite.isEmpty()) {
            Site foundedSite = selectedSiteIsIndexed(existingSites, selectedSite);
            if (foundedSite == null) {
                throw new NotFoundIndexedSiteException(TEMPLATE_API_SEARCH_EMPTY_QUERY);
            }
            List<Site> sites = new ArrayList<>();
            sites.add(foundedSite);
            return processSearchSites(sites,
                    query,
                    offset,
                    limit);
        }

        boolean allSitesIsIndexed = existingSites
                .stream()
                .allMatch(site ->
                        site.getStatus().equals(SiteStatusType.INDEXED));

        if (!allSitesIsIndexed) {
            throw new NotFoundIndexedSiteException(TEMPLATE_API_SEARCH_EMPTY_QUERY);
        }

        return processSearchSites(existingSites, query, offset, limit);
    }

    private Site selectedSiteIsIndexed(List<Site> existingSites, String selectedSite) {
        for (Site site : existingSites) {
            if (selectedSite.matches(site.getUrl())) {
                if (site.getStatus().equals(SiteStatusType.INDEXED)) {
                    return site;
                }
            }
        }
        return null;
    }

    private ResponseSearchContainer processSearchSites(List<Site> sites,
                                                            String query,
                                                            Integer offset,
                                                            Integer limit) {
        List<String> lemmas = splitQueryToWords(query);

        if (lemmas.isEmpty()) {
            throw new IncorrectQuerySearchException(TEMPLATE_API_QUERY_DOES_NOT_CONTAINS_CORRECT_LEMMAS);
        }

        log.info(TEMPLATE_SEARCH_COLLECTED_LEMMAS,
                Arrays.toString(lemmas.toArray()));

        List<Integer> sitesId = sites
                .stream()
                .map(Site::getId)
                .toList();

        List<Lemma> rareLemmas = collectRareLemmas(sitesId, lemmas);

        List<Integer> pagesIds = null;
        List<Page> pages = new ArrayList<>();

        for (Lemma lemma : rareLemmas) {
            if (pagesIds == null) {
                pagesIds = indexesComponent.findAllPageIdsBySiteIdInAndLemma(sitesId, lemma.getLemma());
                Iterable<Page> pageIter = pagesComponent.getPagesByIds(pagesIds);
                pageIter.forEach(pages::add);
            }
        }

        List<DataPage> dataPageList = calculateAndCreateDataPageList(query, pages, rareLemmas);

        ResponseSearchContainer responseContainer = new ResponseSearchContainer(true,
                dataPageList.size(),
                dataPageList);

//        log.info(TEMPLATE_SERVICE_RESPONSE,
//                responseContainer,
//                HttpStatusCode.valueOf(HTTP_CODE_OK));

        return responseContainer;
    }

    private List<String> splitQueryToWords(String query) {
        Map<String, Integer> mapOfLemmas = new HashMap<>();
        for (LuceneMorphologyComponent.Lang lang : LuceneMorphologyComponent.Lang.values()) {
            mapOfLemmas.putAll(luceneMorphologyComponent.calculateLemmas(query,
                    lang));
        }
        return mapOfLemmas.keySet()
                .stream()
                .toList();
    }

    private List<Lemma> collectRareLemmas(List<Integer> sitesId, List<String> lemmas) {
        Iterable<Lemma> lemmaIter =
                lemmasComponent.findAllBySiteIdInAndLemmaIn(sitesId, lemmas);

        Integer countPages = pagesComponent.countAllBySiteIdIn(sitesId);

        List<Lemma> rareLemmas = new ArrayList<>();

        for (Lemma lemma : lemmaIter) {
            List<Integer> lemmaIds =
                    lemmasComponent.findAllIdBySiteIdInAndLemma(sitesId, lemma.getLemma());

            Integer countLemmaInPagesIndexes =
                    indexesComponent.countAllByLemmaIdIn(lemmaIds);

            int curPercentLemmaFound =
                    (int) (countLemmaInPagesIndexes * 100.0 / countPages);

            boolean lemmaIsRare =
                    curPercentLemmaFound < searchConfig.getPercentIgnoreLemmasTooMatchFound();

            log.info(TEMPLATE_SEARCH_CALCULATE_PERCENT_FOR_LEMMA,
                    lemma.getLemma(),
                    countPages,
                    countLemmaInPagesIndexes,
                    curPercentLemmaFound +
                            (lemmaIsRare ? " < " : " > ") +
                            searchConfig.getPercentIgnoreLemmasTooMatchFound());

            if (lemmaIsRare) {
                rareLemmas.add(lemma);
            }
        }

        rareLemmas = rareLemmas
                .stream()
                .sorted(Comparator.comparingInt(Lemma::getFrequency))
                .collect(Collectors.toList());

        log.info(TEMPLATE_SEARCH_COLLECTED_RARE_LEMMAS,
                Arrays.toString(rareLemmas.toArray()));
        return rareLemmas;
    }

    private List<DataPage> calculateAndCreateDataPageList(String query, List<Page> pages, List<Lemma> rareLemmas) {
        List<DataPage> dataPageList = new ArrayList<>();
        float maxRelevance = 0;

        for (Page page : pages) {
            float absoluteRelevance = 0f;
            float relativeRelevance = 0f;
            StringBuilder builderForLog = new StringBuilder();
            String snippet = "";

            for (Lemma lemma : rareLemmas) {
                Index index = indexesComponent.findFirstByPageIdAndLemmaId(page.getId(), lemma.getId());

                if (index != null) {
                    absoluteRelevance = Float.sum(absoluteRelevance, index.getRank());
                    maxRelevance = Float.max(maxRelevance, absoluteRelevance);

                    if (!builderForLog.isEmpty()) {
                        builderForLog.append("; ");
                    }

                    builderForLog
                            .append(lemma.getLemma())
                            .append(" - ")
                            .append(index.getRank());
                }
                snippet = JsoupUtility.getSnippetFromHTML(page.getContent(), query);
            }

            log.info(TEMPLATE_SEARCH_CALCULATED_PAGE_RELEVANCE,
                    page.getId(),
                    builderForLog,
                    absoluteRelevance,
                    relativeRelevance);

            DataPage dataPage = new DataPage(
                    page.getSite().getUrl().substring(0,
                            page.getSite().getUrl().length() - 1),
                    page.getSite().getName(),
                    page.getPath(),
                    JsoupUtility.getTitleFromHTML(page.getContent()),
                    snippet,
                    absoluteRelevance / maxRelevance
            );

            dataPageList.add(dataPage);
        }

        return dataPageList
                .stream()
                .sorted(Comparator.comparing(DataPage::relevance).reversed())
                .collect(Collectors.toList());
    }
}
