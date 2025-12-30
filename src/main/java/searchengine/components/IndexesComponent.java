package searchengine.components;

import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;

import java.util.List;
import java.util.Map;

public interface IndexesComponent {
    void deleteFirstFoundIndexByPageIdAndLemmaIdInDB(Integer pageId, Integer lemmaId);
    void deleteIndexesByPageIdInDB(Integer pageId);
    void deleteAllByPageIdIn(List<Integer> ids);
    Index selectOrInsertIndexToDB(Index index);
    Index saveIndexToDB(Index index);
    Iterable<Index> saveIndexesToDB(List<Index> indexes);
    Iterable<Index> prepareAndSaveIndexes(Map<String, Integer> mapOfLemmas, Iterable <Lemma> lemmaIterable, Page page);
    List<Integer> findAllIdsBySiteId(Integer siteId);
    Iterable<Index> findAllById(List<Integer> ids);
    Iterable<Index> findAllByPageId(Integer pageId);
    Integer countAllByLemmaIdIn(List<Integer> lemmaId);
    List<Integer> findAllPageIdsBySiteIdInAndLemmaId(List<Integer> sitesId, Integer lemmaId);
    List<Integer> findAllPageIdsBySiteIdInAndLemma(List<Integer> sitesId, String lemma);
    Index findFirstByPageIdAndLemmaId(Integer pageId, Integer lemmaId);
}
