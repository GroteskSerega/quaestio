package searchengine.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.entity.Index;
import searchengine.entity.Lemma;
import searchengine.entity.Page;
import searchengine.repository.IndexRepository;

import java.util.*;

import static searchengine.component.ComponentLoggingTemplates.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class IndexesComponentImpl implements IndexesComponent {
    private final IndexRepository indexRepository;

    @Override
    @Transactional
    public void deleteIndexesByPageIdInDB(Integer pageId) {
        log.info(TEMPLATE_REPOSITORY_INDEXES_TRY_TO_DELETE_BY_PAGE_ID,
                pageId);
        indexRepository.deleteByPageId(pageId);
        log.info(TEMPLATE_REPOSITORY_INDEXES_DELETED_BY_PAGE_ID,
                pageId);
    }

    @Override
    @Transactional
    public void deleteBySiteIds(List<Integer> siteIds) {
        log.info(TEMPLATE_REPOSITORY_INDEXES_TRY_TO_DELETE_BY_SITES_IDS,
                Arrays.toString(siteIds.toArray()));
        indexRepository.deleteBySiteIds(siteIds);
        log.info(TEMPLATE_REPOSITORY_INDEXES_DELETED_BY_SITES_IDS,
                Arrays.toString(siteIds.toArray()));
    }

    @Override
    @Transactional
    public List<Index> prepareAndSaveIndexes(Map<String, Integer> mapOfLemmas,
                                                  Iterable <Lemma> lemmaIterable,
                                                  Page page) {
        List<Index> newIndexes = new ArrayList<>();

        for (Lemma lemma : lemmaIterable) {
            Index newIndex = new Index();
            newIndex.setLemma(lemma);
            newIndex.setPage(page);
            newIndex.setRank(Float.valueOf(mapOfLemmas.get(lemma.getLemma())));
//            newIndexes.add(selectOrInsertIndexToDB(newIndex));
            newIndexes.add(newIndex);
        }

        return indexRepository.saveAll(newIndexes);
    }

    @Override
    public List<Index> findAllByPageId(Integer pageId) {
        List<Index> indexes = indexRepository.findAllByPageId(pageId);
//        indexIter.forEach(index ->
//                log.info(TEMPLATE_REPOSITORY_INDEXES_FOUND,
//                        index.getId()));
        return indexes;
    }

    @Override
    public Integer countAllByLemmaIdIn(List<Integer> lemmaId) {
        Integer countRows = indexRepository.countAllByLemmaIdIn(lemmaId);
        log.info(TEMPLATE_REPOSITORY_INDEXES_COUNT_ROWS_BY_LEMMAS_ID,
                countRows,
                Arrays.toString(lemmaId.toArray()));
        return countRows;
    }

    @Override
    public List<Integer> findAllPageIdsBySiteIdInAndLemma(List<Integer> sitesId, String lemma) {
        List<Integer> pagesIds = indexRepository.findAllPageIdsBySiteIdInAndLemma(sitesId, lemma);
        log.info(TEMPLATE_REPOSITORY_INDEXES_FOUND_PAGE_IDS_BY_SITE_IDS_AND_LEMMA_ID,
                Arrays.toString(pagesIds.toArray()),
                Arrays.toString(sitesId.toArray()),
                lemma);
        return pagesIds;
    }

    @Override
    public Index findFirstByPageIdAndLemmaId(Integer pageId, Integer lemmaId) {
        Index index = indexRepository.findFirstByPageIdAndLemmaId(pageId, lemmaId);
        log.info(TEMPLATE_REPOSITORY_INDEXES_FOUND_BY_LEMMA_ID_AND_PAGE_ID,
                lemmaId,
                pageId,
                index != null ? index.getId() : null);
        return index;
    }
}
