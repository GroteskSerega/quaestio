package searchengine.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.config.SiteConfig;
import searchengine.config.SitesListConfig;
import searchengine.entity.Site;
import searchengine.entity.SiteStatusType;
import searchengine.repository.SiteRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static searchengine.component.ComponentLoggingTemplates.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
public class SitesComponentImpl implements SitesComponent {

    private final SitesListConfig sitesListConfig;
    private final SiteRepository siteRepository;

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
    @Transactional
    public List<Site> saveSitesToDB(List<Site> sites) {
        log.info(TEMPLATE_REPOSITORY_SITES_TRY_TO_SAVE,
                sites.stream()
                        .map(Site::getName)
                        .collect(Collectors.joining(",")));
        List<Site> savedSites = siteRepository.saveAll(sites);
        log.info(TEMPLATE_REPOSITORY_SITES_SAVED,
                sites.stream()
                        .map(Site::getName)
                        .collect(Collectors.joining(",")));
        return savedSites;
    }

    @Override
    @Transactional
    public Site saveSiteToDB(Site site) {
        log.info(TEMPLATE_REPOSITORY_SITES_TRY_TO_SAVE,
                site);
        Site savedSite = siteRepository.save(site);
        log.info(TEMPLATE_REPOSITORY_SITES_SAVED,
                savedSite);
        return savedSite;
    }

    @Override
    public List<Site> getExistingSitesFromDBAndMatchWithConfig() {
        List<Site> existingSites = new ArrayList<>();

        for (SiteConfig siteConfig : sitesListConfig.getSites()) {
            Site foundedSite = siteRepository.findByName(siteConfig.getName());
            if (foundedSite != null) {
                existingSites.add(foundedSite);
            }
        }
        log.info(TEMPLATE_REPOSITORY_SITES_FOUND,
                existingSites.stream()
                        .map(Site::getName)
                        .collect(Collectors.joining(",")));
        return existingSites;
    }

    @Override
    @Transactional
    public void deleteByIds(List<Integer> ids) {
        log.info(TEMPLATE_REPOSITORY_SITES_TRY_TO_DELETE,
                Arrays.toString(ids.toArray()));
        siteRepository.deleteByIds(ids);
        log.info(TEMPLATE_REPOSITORY_SITES_DELETED,
                Arrays.toString(ids.toArray()));
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

        Site foundedSite = siteRepository.findByName(matchConfig.getName());
        log.info(TEMPLATE_REPOSITORY_SITES_FOUND, foundedSite);

        if (foundedSite != null) {
            return foundedSite;
        }

        Site newSite = generateSite(matchConfig, SiteStatusType.INDEXING);
        return saveSiteToDB(newSite);
    }

    @Override
    public List<Site> getSitesFromDBMatchWithConfigAndOtherSitesFromConfig() {
        List<Site> sites = new ArrayList<>();

        for (SiteConfig siteConfig : sitesListConfig.getSites()) {
            Site foundedSite = siteRepository.findByName(siteConfig.getName());
            if (foundedSite != null) {
                sites.add(foundedSite);
                continue;
            }
            Site site = new Site();
            site.setName(siteConfig.getName());
            site.setUrl(siteConfig.getUrl());
            sites.add(site);
        }

        log.info(TEMPLATE_REPOSITORY_SITES_FOUND_IN_DB_AND_CONFIG,
                sites.stream()
                        .map(Site::getName)
                        .collect(Collectors.joining(",")));

        return sites;
    }
}
