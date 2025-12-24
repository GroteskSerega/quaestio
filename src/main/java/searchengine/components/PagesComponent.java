package searchengine.components;

import searchengine.model.Page;
import searchengine.model.Site;

import java.util.List;

public interface PagesComponent {
    Iterable<Page> savePagesToDB(List<Page> pages);
    Page savePageToDB(Page page);
    void deletePagesInDB(List<Site> sites);
    Page findFirstPageBySiteIdAndPath(Integer siteId, String path);
    Page findFirstPageBySiteId(Integer siteId);
    void readAndInsertPage(Integer siteId, Page page);
}
