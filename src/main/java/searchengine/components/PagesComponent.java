package searchengine.components;

import searchengine.model.Page;
import searchengine.model.Site;

import java.util.List;

public interface PagesComponent {
    Iterable<Page> savePagesToDB(List<Page> pages);
    Page savePageToDB(Page page);
    void deletePagesInDB(List<Site> sites);
    void deletePageBySiteIdAndPathInDB(Integer siteId, String path);
    Page findFirstPageBySiteIdAndPathInDB(Integer siteId, String path);
    Page findFirstPageBySiteIdInDB(Integer siteId);
    Page selectOrInsertPageToDB(Page page);
    Integer countAllBySiteId(Integer siteId);
    Integer countAllBySiteIdIn(List<Integer> sitesId);
    List<Page> getPagesBySiteId(Integer siteId);
    Iterable<Page> getPagesByIds(List<Integer> ids);
    List<Integer> findAllIdsBySiteId(Integer siteId);
}
