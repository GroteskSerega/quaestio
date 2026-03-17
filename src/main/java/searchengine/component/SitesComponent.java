package searchengine.component;

import searchengine.config.SiteConfig;
import searchengine.entity.Site;
import searchengine.entity.SiteStatusType;

import java.util.List;

public interface SitesComponent {
    List<Site> createSitesFromConfigForIndexing();
    Site generateSite(SiteConfig siteConfig, SiteStatusType status);
    Iterable<Site> saveSitesToDB(List<Site> sites);
    Site saveSiteToDB(Site site);
    List<Site> getExistingSitesFromDBAndMatchWithConfig();
    void deleteSitesInDB(List<Site> sites);
    boolean validateUrlByConfig(String url);
    Site getSiteMatchUrl(String url);
    List<Site> getSitesFromConfig();
    List<Site> getSitesFromDBMatchWithConfigAndOtherSitesFromConfig();
}
