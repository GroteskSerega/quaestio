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
import java.util.Optional;

import static searchengine.logging.LoggingTemplates.*;
import static searchengine.logging.LoggingTemplates.TEMPLATE_REPOSITORY_SITES_DELETED;
import static searchengine.logging.LoggingTemplates.TEMPLATE_REPOSITORY_SITES_TRY_TO_DELETE;

@Slf4j
@RequiredArgsConstructor
@Component
public class SitesComponentImpl implements SitesComponent {

    private final SitesListConfig sitesListConfig;
    private final SitesRepository sitesRepository;

    public List<Site> createSitesFromConfigForIndexing() {
        List<Site> sites =
                new ArrayList<>(sitesListConfig.getSites().size());

        for (SiteConfig siteConfig : sitesListConfig.getSites()) {
            Site site = new Site();
            site.setName(siteConfig.getName());
            site.setUrl(siteConfig.getUrl());
            site.setStatus(SiteStatusType.INDEXING);
            site.setStatusTime(LocalDateTime.now());
            sites.add(site);
        }
        return sites;
    }

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

    public List<Site> getExistingSitesFromDBAndMatchWithConfig() {
        List<Site> existingSites = new ArrayList<>();

        for (SiteConfig siteConfig : sitesListConfig.getSites()) {
            Site foundedSite = sitesRepository.findByName(siteConfig.getName());
            if (foundedSite != null) {
                Optional<Site> optionalSite =
                        sitesRepository.findById(foundedSite.getId());
                optionalSite.ifPresent(existingSites::add);
            }
        }
        log.info(TEMPLATE_REPOSITORY_SITES_FOUNDED,
                Arrays.toString(existingSites.toArray()));
        return existingSites;
    }

    public void deleteSitesInDB(List<Site> sites) {
        for (Site site : sites) {
            log.info(TEMPLATE_REPOSITORY_SITES_TRY_TO_DELETE,
                    site);
            sitesRepository.deleteById(site.getId());
            log.info(TEMPLATE_REPOSITORY_SITES_DELETED,
                    site);
        }
    }
}
