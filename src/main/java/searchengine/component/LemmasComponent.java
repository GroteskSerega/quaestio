package searchengine.component;

import searchengine.entity.Lemma;
import searchengine.entity.Page;

import java.util.List;
import java.util.Map;

public interface LemmasComponent {
    void deleteBySiteIds(List<Integer> siteIds);
    List<Lemma> saveLemmasToDB(List<Lemma> lemmas);
    List<Lemma> prepareAndSaveLemmas(Map<String, Integer> mapOfLemmas, Page page);
    List<Lemma> findAllBySiteIdInAndLemmaIn(List<Integer> siteId, List<String> lemmas);
    List<Integer> findAllIdBySiteIdInAndLemma(List<Integer> siteId, String lemma);
    void deleteById(Integer id);
}
