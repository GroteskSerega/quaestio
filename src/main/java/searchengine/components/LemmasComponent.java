package searchengine.components;

import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;

import java.util.List;
import java.util.Map;

public interface LemmasComponent {
    void deleteLemmasBySiteIdDB(Integer siteId);
    void deleteLemmasInDB(List<Site> sites);
    void deleteLemmasByIdsInDB(List<Integer> ids);
    Lemma saveLemmaToDB(Lemma lemma);
    Iterable<Lemma> saveLemmasToDB(List<Lemma> lemmas);
    Integer countAllBySiteId(Integer siteId);
    Lemma selectForUpdateOrInsertLemma(Lemma newLemma);
    Iterable<Lemma> selectForUpdateOrInsertLemmas(List<Lemma> newLemma);
    Iterable<Lemma> prepareAndSaveLemmas(Map<String, Integer> mapOfLemmas, Page page);
}
