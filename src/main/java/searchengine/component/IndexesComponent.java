package searchengine.component;

import searchengine.entity.Index;
import searchengine.entity.Lemma;
import searchengine.entity.Page;

import java.util.List;
import java.util.Map;

public interface IndexesComponent {
    void deleteIndexesByPageIdInDB(Integer pageId);
    void deleteBySiteIds(List<Integer> siteIds);
    List<Index> prepareAndSaveIndexes(Map<String, Integer> mapOfLemmas, Iterable <Lemma> lemmaIterable, Page page);
    List<Index> findAllByPageId(Integer pageId);
    Integer countAllByLemmaIdIn(List<Integer> lemmaId);
    List<Integer> findAllPageIdsBySiteIdInAndLemma(List<Integer> sitesId, String lemma);
    Index findFirstByPageIdAndLemmaId(Integer pageId, Integer lemmaId);
}
