package searchengine.components;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repositories.PagesRepository;

import java.util.List;

import static searchengine.logging.LoggingTemplates.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class PagesComponentImpl implements PagesComponent {

    private final PagesRepository pagesRepository;

    @Override
    public Iterable<Page> savePagesToDB(List<Page> pages) {
        return null;
    }

    @Override
    public Page savePageToDB(Page page) {
        log.info(TEMPLATE_REPOSITORY_PAGES_TRY_TO_SAVE,
                page.getPath());
        Page savedPage = pagesRepository.save(page);
        log.info(TEMPLATE_REPOSITORY_PAGES_SAVED,
                savedPage.getId());
        return savedPage;
    }

    @Override
    public void deletePagesInDB(List<Site> sites) {
        for (Site site : sites) {
            log.info(TEMPLATE_REPOSITORY_PAGES_TRY_TO_DELETE_BY_SITE_ID,
                    site.getId());
            pagesRepository.deleteAllBySiteId(site.getId());
            log.info(TEMPLATE_REPOSITORY_PAGES_DELETED_BY_SITE_ID,
                    site.getId());
        }
    }

    @Override
    public Page findFirstPageBySiteIdAndPath(Integer siteId,
                                             String path) {
        Page foundedPage =
                pagesRepository.findFirstBySiteIdAndPath(siteId,
                        path);
        log.info(TEMPLATE_REPOSITORY_PAGES_FIRST_FOUNDED_BY_SITE_ID_AND_URI,
                siteId,
                path,
                foundedPage != null ? foundedPage.getId() : null);
        return foundedPage;
    }

    @Override
    public Page findFirstPageBySiteId(Integer siteId) {
        Page foundedPage =
                pagesRepository.findFirstBySiteId(siteId);
        log.info(TEMPLATE_REPOSITORY_PAGES_FIRST_FOUNDED_BY_SITE_ID,
                siteId,
                foundedPage != null ? foundedPage.getId() : null);
        return foundedPage;
    }

    @Transactional
    @Override
    public void readAndInsertPage(Integer siteId, Page page) {
        Page existindPage =
                pagesRepository.findFirstBySiteIdAndPath(siteId,
                        page.getPath());
        if (existindPage == null) {
            pagesRepository.save(page);
        }
    }
}
