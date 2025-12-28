package searchengine.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repositories.IndexesRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static searchengine.logging.LoggingTemplates.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class IndexesComponentImpl implements IndexesComponent {
    private final IndexesRepository indexesRepository;

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
    public Index saveIndexToDB(Index index) {
        log.info(TEMPLATE_REPOSITORY_INDEXES_TRY_TO_SAVE, index);
        Index savedIndex = indexesRepository.save(index);
        log.info(TEMPLATE_REPOSITORY_INDEXES_SAVED, savedIndex);
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
            newIndexes.add(newIndex);
        }
        return saveIndexesToDB(newIndexes);
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
                log.info(TEMPLATE_REPOSITORY_INDEXES_FOUNDED,
                        index.getId()));
        return indexIter;
    }

    @Override
    public Iterable<Index> findAllByPageId(Integer pageId) {
        Iterable<Index> indexIter = indexesRepository.findAllByPageId(pageId);
        indexIter.forEach(index ->
                log.info(TEMPLATE_REPOSITORY_INDEXES_FOUNDED,
                        index.getId()));
        return indexIter;
    }
}
