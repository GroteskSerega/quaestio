package searchengine.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repositories.IndexesRepository;

import java.util.*;

import static searchengine.logging.LoggingTemplates.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class IndexesComponentImpl implements IndexesComponent {
    private final IndexesRepository indexesRepository;

    private static final Object SYNC_SELECT_OR_INSERT_INDEX_TO_DB = new Object();

    @Override
    public void deleteFirstFoundIndexByPageIdAndLemmaIdInDB(Integer pageId, Integer lemmaId) {
        log.info(TEMPLATE_REPOSITORY_INDEXES_TRY_TO_DELETE_BY_PAGE_ID_AND_LEMMA_ID,
                pageId,
                lemmaId);
        indexesRepository.deleteFirstByPageIdAndLemmaId(pageId, lemmaId);
        log.info(TEMPLATE_REPOSITORY_INDEXES_DELETED_BY_PAGE_ID_AND_LEMMA_ID,
                pageId,
                lemmaId);
    }

    @Override
    public void deleteIndexesByPageIdInDB(Integer pageId) {
        log.info(TEMPLATE_REPOSITORY_INDEXES_TRY_TO_DELETE_BY_PAGE_ID,
                pageId);
        indexesRepository.deleteAllByPageId(pageId);
        log.info(TEMPLATE_REPOSITORY_INDEXES_DELETED_BY_PAGE_ID,
                pageId);
    }

    @Override
    public void deleteAllByPageIdIn(List<Integer> ids) {
        log.info(TEMPLATE_REPOSITORY_LEMMAS_TRY_TO_DELETE_BY_IDS,
                Arrays.toString(ids.toArray()));
        indexesRepository.deleteAllByPageIdIn(ids);
        log.info(TEMPLATE_REPOSITORY_LEMMAS_DELETED_BY_IDS,
                Arrays.toString(ids.toArray()));
    }

    @Override
    public Index selectOrInsertIndexToDB(Index index) {
        Index newIndex;
        synchronized (SYNC_SELECT_OR_INSERT_INDEX_TO_DB) {
            Optional<Index> indexOpt =
                    indexesRepository.findByLemmaIdAndPageId(index.getLemma().getId(),
                            index.getLemma().getId());
            newIndex = indexOpt.orElseGet(() -> saveIndexToDB(index));
        }
        log.info(TEMPLATE_REPOSITORY_INDEXES_FOUND_BY_LEMMA_ID_AND_PAGE_ID,
                index.getLemma().getId(),
                index.getPage().getId(),
                index.getId());
        return newIndex;
    }

    @Override
    public Index saveIndexToDB(Index index) {
        log.info(TEMPLATE_REPOSITORY_INDEXES_TRY_TO_SAVE, index.getLemma().getLemma());
        Index savedIndex = indexesRepository.save(index);
        log.info(TEMPLATE_REPOSITORY_INDEXES_SAVED, savedIndex.getId());
        return savedIndex;
    }

    @Override
    public Iterable<Index> saveIndexesToDB(List<Index> indexes) {
        indexes.forEach(index ->
                log.info(TEMPLATE_REPOSITORY_INDEXES_TRY_TO_SAVE, index.getLemma()));
        Iterable<Index> indexIterable = indexesRepository.saveAll(indexes);
        indexIterable.forEach(index ->
                log.info(TEMPLATE_REPOSITORY_INDEXES_SAVED, index.getId()));
        return indexIterable;
    }

    @Override
    public Iterable<Index> prepareAndSaveIndexes(Map<String, Integer> mapOfLemmas,
                                                  Iterable <Lemma> lemmaIterable,
                                                  Page page) {
        List<Index> newIndexes = new ArrayList<>();

        for (Lemma lemma : lemmaIterable) {
            Index newIndex = new Index();
            newIndex.setLemma(lemma);
            newIndex.setPage(page);
            newIndex.setRank(Float.valueOf(mapOfLemmas.get(lemma.getLemma())));
            newIndexes.add(selectOrInsertIndexToDB(newIndex));
        }
        return newIndexes;
    }

    @Override
    public List<Integer> findAllIdsBySiteId(Integer siteId) {
        List<Integer> ids = indexesRepository.findAllIdsBySiteId(siteId);
        log.info(TEMPLATE_REPOSITORY_INDEXES_COUNT_BY_SITE_ID,
                Arrays.toString(ids.toArray()),
                siteId);
        return ids;
    }

    @Override
    public Iterable<Index> findAllById(List<Integer> ids) {
        Iterable<Index> indexIter = indexesRepository.findAllById(ids);
        indexIter.forEach(index ->
                log.info(TEMPLATE_REPOSITORY_INDEXES_FOUND,
                        index.getId()));
        return indexIter;
    }

    @Override
    public Iterable<Index> findAllByPageId(Integer pageId) {
        Iterable<Index> indexIter = indexesRepository.findAllByPageId(pageId);
        indexIter.forEach(index ->
                log.info(TEMPLATE_REPOSITORY_INDEXES_FOUND,
                        index.getId()));
        return indexIter;
    }

    @Override
    public Integer countAllByLemmaIdIn(List<Integer> lemmaId) {
        Integer countRows = indexesRepository.countAllByLemmaIdIn(lemmaId);
        log.info(TEMPLATE_REPOSITORY_INDEXES_COUNT_ROWS_BY_LEMMAS_ID,
                countRows,
                Arrays.toString(lemmaId.toArray()));
        return countRows;
    }

    @Override
    public List<Integer> findAllPageIdsBySiteIdInAndLemmaId(List<Integer> sitesId, Integer lemmaId) {
        List<Integer> pagesIds = indexesRepository.findAllPageIdsBySiteIdInAndLemmaId(sitesId, lemmaId);
        log.info(TEMPLATE_REPOSITORY_INDEXES_FOUND_PAGE_IDS_BY_SITE_IDS_AND_LEMMA_ID,
                Arrays.toString(pagesIds.toArray()),
                Arrays.toString(sitesId.toArray()),
                lemmaId);
        return pagesIds;
    }

    @Override
    public List<Integer> findAllPageIdsBySiteIdInAndLemma(List<Integer> sitesId, String lemma) {
        List<Integer> pagesIds = indexesRepository.findAllPageIdsBySiteIdInAndLemma(sitesId, lemma);
        log.info(TEMPLATE_REPOSITORY_INDEXES_FOUND_PAGE_IDS_BY_SITE_IDS_AND_LEMMA_ID,
                Arrays.toString(pagesIds.toArray()),
                Arrays.toString(sitesId.toArray()),
                lemma);
        return pagesIds;
    }

    @Override
    public Index findFirstByPageIdAndLemmaId(Integer pageId, Integer lemmaId) {
        Index index = indexesRepository.findFirstByPageIdAndLemmaId(pageId, lemmaId);
        log.info(TEMPLATE_REPOSITORY_INDEXES_FOUND_BY_LEMMA_ID_AND_PAGE_ID,
                lemmaId,
                pageId,
                index != null ? index.getId() : null);
        return index;
    }
}
