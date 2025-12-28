package searchengine.components;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repositories.PagesRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static searchengine.logging.LoggingTemplates.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class PagesComponentImpl implements PagesComponent {

    private final PagesRepository pagesRepository;

    private static final Object SYNC_SELECT_OR_INSERT_PAGE_TO_DB = new Object();

    @Override
    public Iterable<Page> savePagesToDB(List<Page> pages) {
        pages.forEach(page ->
                log.info(TEMPLATE_REPOSITORY_PAGES_TRY_TO_SAVE,
                        page.getSite().getId(),
                        page.getPath()));
        Iterable<Page> pageIterable = pagesRepository.saveAll(pages);
        pageIterable.forEach(iter ->
                log.info(TEMPLATE_REPOSITORY_PAGES_SAVED,
                        iter.getId(),
                        iter.getSite().getId(),
                        iter.getPath()));
        return pageIterable;
    }

    @Override
    public Page savePageToDB(Page page) {
        log.info(TEMPLATE_REPOSITORY_PAGES_TRY_TO_SAVE,
                page.getSite().getId(),
                page.getPath());
        Page savedPage = pagesRepository.save(page);
        log.info(TEMPLATE_REPOSITORY_PAGES_SAVED,
                savedPage.getId(),
                savedPage.getSite().getId(),
                savedPage.getPath());
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
    public void deletePageBySiteIdAndPathInDB(Integer siteId, String path) {
        log.info(TEMPLATE_REPOSITORY_PAGES_TRY_TO_DELETE_BY_SITE_ID_AND_PATH,
                siteId,
                path);
        pagesRepository.deleteFirstBySiteIdAndPath(siteId, path);
        log.info(TEMPLATE_REPOSITORY_PAGES_DELETED_BY_SITE_ID_AND_PATH,
                siteId,
                path);
    }

    @Override
    public Page findFirstPageBySiteIdAndPathInDB(Integer siteId,
                                                 String path) {
        Optional<Page> pageOpt =
                pagesRepository.findFirstBySiteIdAndPath(siteId,
                        path);
        Page newPage = pageOpt.orElse(null);
        log.info(TEMPLATE_REPOSITORY_PAGES_FOUNDED_BY_SITE_ID_AND_URI,
                siteId,
                path,
                newPage != null ? newPage.getId() : null);
        return newPage;
    }

    @Override
    public Page findFirstPageBySiteIdInDB(Integer siteId) {
        Page foundedPage =
                pagesRepository.findFirstBySiteId(siteId);
        log.info(TEMPLATE_REPOSITORY_PAGES_FIRST_FOUNDED_BY_SITE_ID,
                siteId,
                foundedPage != null ? foundedPage.getId() : null);
        return foundedPage;
    }

    @Transactional
    @Override
    public Page selectOrInsertPageToDB(Page page) {
        // TODO change current logic to PESSIMISTIC_WRITE OR SOMETHING
        // CURRENT LOGIC:
        // -:
        // NOT EFFECTIVE FOR SEVERAL SITES
        // NOT WORKED FOR SEVERAL INSTANCE OF THESE APP
        // +:
        // NOT CREATE DUPLICATE OF PAGES IN DATABASE
        Page newPage;
        synchronized (SYNC_SELECT_OR_INSERT_PAGE_TO_DB) {
            Optional<Page> pageOpt =
                    pagesRepository.findBySiteIdAndPath(page.getSite().getId(),
                            page.getPath());
            newPage = pageOpt.orElseGet(() -> savePageToDB(page));
        }
        log.info(TEMPLATE_REPOSITORY_PAGES_FOUNDED_BY_SITE_ID_AND_URI,
                page.getSite().getId(),
                page.getPath(),
                newPage.getId());
        return newPage;
    }

    @Override
    public Integer countAllBySiteId(Integer siteId) {
        Integer countPages = pagesRepository.countAllBySiteId(siteId);
        log.info(TEMPLATE_REPOSITORY_PAGES_COUNT_BY_SITE_ID,
                countPages,
                countPages);
        return countPages;
    }

    @Override
    public List<Page> getPagesBySiteId(Integer siteId) {
        List<Page> foundedPages = pagesRepository.getAllBySiteId(siteId);
        log.info(TEMPLATE_REPOSITORY_PAGES_FOUNDED_LIST_IDS_BY_SITE_ID,
                Arrays.toString(foundedPages
                        .stream()
                        .map(Page::getPath)
                        .toArray()));
        return foundedPages;
    }

    @Override
    public List<Integer> findAllIdsBySiteId(Integer siteId) {
        List<Integer> ids = pagesRepository.findAllIdsBySiteId(siteId);
        log.info(TEMPLATE_REPOSITORY_PAGES_COUNT_BY_SITE_ID,
                Arrays.toString(ids.toArray()),
                siteId);
        return ids;
    }
}
