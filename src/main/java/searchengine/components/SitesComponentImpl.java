package searchengine.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.config.SiteConfig;
import searchengine.config.SitesListConfig;
import searchengine.model.Site;
import searchengine.model.SiteStatusType;
import searchengine.repositories.SitesRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static searchengine.logging.LoggingTemplates.*;
import static searchengine.logging.LoggingTemplates.TEMPLATE_REPOSITORY_SITES_DELETED;
import static searchengine.logging.LoggingTemplates.TEMPLATE_REPOSITORY_SITES_TRY_TO_DELETE;

@Slf4j
@RequiredArgsConstructor
@Component
public class SitesComponentImpl implements SitesComponent {

    private final SitesListConfig sitesListConfig;
    private final SitesRepository sitesRepository;

    private static final String TEMPLATE_REGEX_VERTEX_LINK = "%s(.+|)";

    @Override
    public List<Site> createSitesFromConfigForIndexing() {
        List<Site> sites =
                new ArrayList<>(sitesListConfig.getSites().size());

        for (SiteConfig siteConfig : sitesListConfig.getSites()) {
            Site site = generateSite(siteConfig, SiteStatusType.INDEXING);
            sites.add(site);
        }
        return sites;
    }

    @Override
    public Site generateSite(SiteConfig siteConfig, SiteStatusType status) {
        Site site = new Site();
        site.setName(siteConfig.getName());
        site.setUrl(siteConfig.getUrl());
        site.setStatus(status);
        site.setStatusTime(LocalDateTime.now());
        return site;
    }

    @Override
    public Iterable<Site> saveSitesToDB(List<Site> sites) {
        log.info(TEMPLATE_REPOSITORY_SITES_TRY_TO_SAVE,
                Arrays.toString(sites.toArray()));
        Iterable<Site> savedSites = sitesRepository.saveAll(sites);
        log.info(TEMPLATE_REPOSITORY_SITES_SAVED,
                Arrays.toString(List.of(savedSites).toArray()));
        return savedSites;
    }

    @Override
    public Site saveSiteToDB(Site site) {
        log.info(TEMPLATE_REPOSITORY_SITES_TRY_TO_SAVE,
                site);
        Site savedSite = sitesRepository.save(site);
        log.info(TEMPLATE_REPOSITORY_SITES_SAVED,
                savedSite);
        return savedSite;
    }

    @Override
    public List<Site> getExistingSitesFromDBAndMatchWithConfig() {
        List<Site> existingSites = new ArrayList<>();

        for (SiteConfig siteConfig : sitesListConfig.getSites()) {
            Site foundedSite = sitesRepository.findByName(siteConfig.getName());
            if (foundedSite != null) {
                existingSites.add(foundedSite);
            }
        }
        log.info(TEMPLATE_REPOSITORY_SITES_FOUNDED,
                Arrays.toString(existingSites.toArray()));
        return existingSites;
    }

    @Override
    public void deleteSitesInDB(List<Site> sites) {
        for (Site site : sites) {
            log.info(TEMPLATE_REPOSITORY_SITES_TRY_TO_DELETE,
                    site);
            sitesRepository.deleteById(site.getId());
            log.info(TEMPLATE_REPOSITORY_SITES_DELETED,
                    site);
        }
    }

    @Override
    public boolean validateUrlByConfig(String url) {
        return sitesListConfig.getSites()
                .stream()
                .anyMatch(siteConfig ->
                        url.matches(String.format(TEMPLATE_REGEX_VERTEX_LINK,
                                siteConfig.getUrl())));
    }

    @Override
    public Site getSiteMatchUrl(String url) {
        SiteConfig matchConfig = null;
        for (SiteConfig siteConfig : sitesListConfig.getSites()) {
            boolean matchSiteConfig = url.matches(String.format(TEMPLATE_REGEX_VERTEX_LINK,
                    siteConfig.getUrl()));
            if (matchSiteConfig) {
                matchConfig = siteConfig;
                break;
            }
        }

        if (matchConfig == null) {
            return null;
        }

        Site foundedSite = sitesRepository.findByName(matchConfig.getName());
        log.info(TEMPLATE_REPOSITORY_SITES_FOUNDED, foundedSite);

        if (foundedSite != null) {
            return foundedSite;
        }

        Site newSite = generateSite(matchConfig, SiteStatusType.INDEXING);
        return saveSiteToDB(newSite);
    }

    @Override
    public List<Site> getSitesFromConfig() {
        List<Site> sites = new ArrayList<>();
        for (SiteConfig siteConfig :sitesListConfig.getSites()) {
            Site site = new Site();
            site.setName(siteConfig.getName());
            site.setUrl(siteConfig.getUrl());
            sites.add(site);
        }
        return sites;
    }

    @Override
    public List<Site> getSitesFromDBMatchWithConfigAndOtherSitesFromConfig() {
        List<Site> sites = new ArrayList<>();

        for (SiteConfig siteConfig : sitesListConfig.getSites()) {
            Site foundedSite = sitesRepository.findByName(siteConfig.getName());
            if (foundedSite != null) {
                sites.add(foundedSite);
                continue;
            }
            Site site = new Site();
            site.setName(siteConfig.getName());
            site.setUrl(siteConfig.getUrl());
            sites.add(site);
        }
        log.info(TEMPLATE_REPOSITORY_SITES_FOUNDED_IN_DB_AND_CONFIG,
                Arrays.toString(sites.toArray()));
        return sites;
    }
}
