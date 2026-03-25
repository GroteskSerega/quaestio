package searchengine.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.entity.Page;
import searchengine.exception.PageAlreadyExists;
import searchengine.repository.PageRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static searchengine.component.ComponentLoggingTemplates.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class PagesComponentImpl implements PagesComponent {
    private final PageRepository pageRepository;

    @Override
    @Transactional
    public Page savePageToDB(Page page) {
        log.info(TEMPLATE_REPOSITORY_PAGES_TRY_TO_SAVE,
                page.getSite().getUrl(),
                page.getPath());
        Page savedPage = pageRepository.save(page);
        log.info(TEMPLATE_REPOSITORY_PAGES_SAVED,
                savedPage.getId(),
                savedPage.getSite().getUrl(),
                savedPage.getPath());
        return savedPage;
    }

    @Override
    @Transactional
    public void deleteBySiteIds(List<Integer> siteIds) {
        log.info(TEMPLATE_REPOSITORY_PAGES_TRY_TO_DELETE_BY_SITES_IDS,
                Arrays.toString(siteIds.toArray()));
        pageRepository.deleteBySiteIds(siteIds);
        log.info(TEMPLATE_REPOSITORY_PAGES_DELETED_BY_SITES_IDS,
                Arrays.toString(siteIds.toArray()));
    }

    @Override
    @Transactional
    public void deletePageBySiteIdAndPathInDB(Integer siteId, String path) {
        log.info(TEMPLATE_REPOSITORY_PAGES_TRY_TO_DELETE_BY_SITE_ID_AND_PATH,
                siteId,
                path);
        pageRepository.deleteFirstBySiteIdAndPath(siteId, path);
        log.info(TEMPLATE_REPOSITORY_PAGES_DELETED_BY_SITE_ID_AND_PATH,
                siteId,
                path);
    }

    @Override
    public Page findFirstPageBySiteIdAndPathInDB(Integer siteId,
                                                 String path) {
        Optional<Page> pageOpt =
                pageRepository.findFirstBySiteIdAndPath(siteId,
                        path);
        Page newPage = pageOpt.orElse(null);
        log.info(TEMPLATE_REPOSITORY_PAGES_FOUND_BY_SITE_ID_AND_URI,
                siteId,
                path,
                newPage != null ? newPage.getId() : null);
        return newPage;
    }

    @Override
    public Page findFirstPageBySiteIdInDB(Integer siteId) {
        Page foundedPage =
                pageRepository.findFirstBySiteId(siteId);
        log.info(TEMPLATE_REPOSITORY_PAGES_FIRST_FOUND_BY_SITE_ID,
                siteId,
                foundedPage != null ? foundedPage.getId() : null);
        return foundedPage;
    }


    /**
     * Hybrid of Optimistic and Pessimistic lock
     * @param page
     * @return
     * @throws PageAlreadyExists
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Page selectOrInsertPage(Page page) throws PageAlreadyExists {
        Optional<Page> existingPage = pageRepository.findBySiteIdAndPath(page.getSite().getId(), page.getPath());

        if (existingPage.isPresent()) {
            throw new PageAlreadyExists("Page already exists");
        }

        try {
            return pageRepository.saveAndFlush(page);
        } catch (DataIntegrityViolationException e) {
            throw new PageAlreadyExists("Page was just inserted by another thread");
//                       return pageRepository.findBySiteIdAndPath(page.getSite().getId(), page.getPath())
//                               .orElseThrow(() -> new RuntimeException("Race condition error during page insertion"));
        }
//        Page existedPage =
//                pagesRepository.findBySiteIdAndPathWithLock(page.getSite().getId(),
//                        page.getPath())
//                        .orElseGet(() -> pagesRepository.save(page));
//
//        log.info(TEMPLATE_REPOSITORY_PAGES_FOUND_BY_SITE_ID_AND_URI,
//                page.getSite().getId(),
//                page.getPath(),
//                existedPage.getId());
//
//        return existedPage;
    }

    @Override
    public Integer countAllBySiteId(Integer siteId) {
        Integer countPages = pageRepository.countAllBySiteId(siteId);
        log.info(TEMPLATE_REPOSITORY_PAGES_COUNT_BY_SITE_ID,
                countPages,
                siteId);
        return countPages;
    }

    @Override
    public Integer countAllBySiteIdIn(List<Integer> sitesId) {
        Integer countPages = pageRepository.countAllBySiteIdIn(sitesId);
        log.info(TEMPLATE_REPOSITORY_PAGES_COUNT_BY_SITE_ID,
                countPages,
                Arrays.toString(sitesId.toArray()));
        return countPages;
    }

    @Override
    public List<Page> getPagesByIds(List<Integer> ids) {
        List<Page> pages = pageRepository.findAllById(ids);
        pages.forEach(page ->
                log.info(TEMPLATE_REPOSITORY_PAGES_FOUND_BY_IDS,
                        page.getId(),
                        Arrays.toString(ids.toArray())));
        return pages;
    }
}
