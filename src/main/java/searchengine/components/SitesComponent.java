package searchengine.components;

import searchengine.model.Site;

import java.util.List;

public interface SitesComponent {
    List<Site> createSitesFromConfigForIndexing();
    Iterable<Site> saveSitesToDB(List<Site> sites);
    Site saveSiteToDB(Site site);
    List<Site> getExistingSitesFromDBAndMatchWithConfig();
    void deleteSitesInDB(List<Site> sites);
}
