package searchengine.component;

import searchengine.entity.Page;

import java.util.List;

public interface PagesComponent {
    Page savePageToDB(Page page);
    void deleteBySiteIds(List<Integer> siteIds);
    void deletePageBySiteIdAndPathInDB(Integer siteId, String path);
    Page findFirstPageBySiteIdAndPathInDB(Integer siteId, String path);
    Page findFirstPageBySiteIdInDB(Integer siteId);
    Page selectOrInsertPage(Page page);
    Integer countAllBySiteId(Integer siteId);
    Integer countAllBySiteIdIn(List<Integer> sitesId);
    List<Page> getPagesByIds(List<Integer> ids);
}
