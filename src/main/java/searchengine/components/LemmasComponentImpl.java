package searchengine.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repositories.LemmasRepository;

import java.util.*;

import static searchengine.logging.LoggingTemplates.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class LemmasComponentImpl implements LemmasComponent {

    private final LemmasRepository lemmasRepository;

    private static final Object SYNC_SELECT_FOR_UPDATE_OR_INSERT_LEMMA = new Object();
    private static final Object SYNC_SELECT_FOR_UPDATE_OR_INSERT_LEMMAS = new Object();

    @Override
    public void deleteLemmasBySiteIdDB(Integer siteId) {
        log.info(TEMPLATE_REPOSITORY_LEMMAS_TRY_TO_DELETE_BY_SITE_ID,
                siteId);
        lemmasRepository.deleteAllBySiteId(siteId);
        log.info(TEMPLATE_REPOSITORY_LEMMAS_DELETED_BY_SITE_ID,
                siteId);
    }

    @Override
    public void deleteLemmasInDB(List<Site> sites) {
        for (Site site : sites) {
            log.info(TEMPLATE_REPOSITORY_LEMMAS_TRY_TO_DELETE_BY_SITE_ID,
                    site.getId());
            lemmasRepository.deleteAllBySiteId(site.getId());
            log.info(TEMPLATE_REPOSITORY_LEMMAS_DELETED_BY_SITE_ID,
                    site.getId());
        }
    }

    @Override
    public void deleteLemmasByIdsInDB(List<Integer> ids) {
        log.info(TEMPLATE_REPOSITORY_INDEXES_TRY_TO_DELETE_BY_PAGE_ID,
                Arrays.toString(ids.toArray()));
        lemmasRepository.deleteAllByIdIn(ids);
        log.info(TEMPLATE_REPOSITORY_INDEXES_DELETED_BY_PAGE_ID,
                Arrays.toString(ids.toArray()));
    }

    @Override
    public Lemma saveLemmaToDB(Lemma lemma) {
        log.info(TEMPLATE_REPOSITORY_LEMMAS_TRY_TO_SAVE,
                lemma);
        Lemma savedLemma = lemmasRepository.save(lemma);
        log.info(TEMPLATE_REPOSITORY_LEMMAS_SAVED,
                savedLemma);
        return savedLemma;
    }

    @Override
    public Iterable<Lemma> saveLemmasToDB(List<Lemma> lemmas) {
        lemmas.forEach(lemma ->
                log.info(TEMPLATE_REPOSITORY_LEMMAS_TRY_TO_SAVE, lemma));
        Iterable<Lemma> lemmaIterable = lemmasRepository.saveAll(lemmas);
        lemmaIterable.forEach(lemma ->
                log.info(TEMPLATE_REPOSITORY_LEMMAS_SAVED, lemma));
        return lemmaIterable;
    }

    @Override
    public Integer countAllBySiteId(Integer siteId) {
        Integer countLemmas = lemmasRepository.countAllBySiteId(siteId);
        log.info(TEMPLATE_REPOSITORY_LEMMAS_COUNT_BY_SITE_ID,
                countLemmas,
                siteId);
        return countLemmas;
    }

//    @Transactional
    @Override
    public Lemma selectForUpdateOrInsertLemma(Lemma lemma) {
        Lemma newLemma;
        // TODO change current logic to PESSIMISTIC_WRITE OR SOMETHING
        // CURRENT LOGIC:
        // -:
        // NOT EFFECTIVE FOR SEVERAL SITES
        // NOT WORKED FOR SEVERAL INSTANCE OF THESE APP
        // +:
        // NOT CREATE DUPLICATE OF lemma IN DATABASE
        synchronized (SYNC_SELECT_FOR_UPDATE_OR_INSERT_LEMMA) {
            Optional<Lemma> lemmaOpt =
                    lemmasRepository.findBySiteIdAndLemma(lemma.getSite().getId(), lemma.getLemma());

            if (lemmaOpt.isPresent()) {
                newLemma = lemmaOpt.get();
                newLemma.setFrequency(newLemma.getFrequency() + 1);
            } else {
                newLemma = new Lemma();
                newLemma.setSite(lemma.getSite());
                newLemma.setLemma(lemma.getLemma());
                newLemma.setFrequency(1);
            }
            lemmasRepository.save(newLemma);
        }
        return newLemma;
    }

//    @Transactional
    @Override
    public Iterable<Lemma> selectForUpdateOrInsertLemmas(List<Lemma> candidatesLemmas) {
        Iterable<Lemma> lemmas;
        // TODO change current logic to PESSIMISTIC_WRITE OR SOMETHING
        // CURRENT LOGIC:
        // -:
        // NOT EFFECTIVE FOR SEVERAL SITES
        // NOT WORKED FOR SEVERAL INSTANCE OF THESE APP
        // +:
        // NOT CREATE DUPLICATE OF lemma IN DATABASE
        synchronized (SYNC_SELECT_FOR_UPDATE_OR_INSERT_LEMMAS) {
            List<Lemma> lemmasForUpdateOrInsert = new ArrayList<>();
            for (Lemma lemma : candidatesLemmas) {
                Optional<Lemma> lemmaOpt =
                        lemmasRepository.findBySiteIdAndLemma(lemma.getSite().getId(), lemma.getLemma());
                Lemma newLemma;
                if (lemmaOpt.isPresent()) {
                    newLemma = lemmaOpt.get();
                    newLemma.setFrequency(newLemma.getFrequency() + 1);
                } else {
                    newLemma = new Lemma();
                    newLemma.setSite(lemma.getSite());
                    newLemma.setLemma(lemma.getLemma());
                    newLemma.setFrequency(1);
                }
                lemmasForUpdateOrInsert.add(newLemma);
            }
            lemmas = lemmasRepository.saveAll(lemmasForUpdateOrInsert);
        }

        return lemmas;
    }

    @Override
    public Iterable<Lemma> prepareAndSaveLemmas(Map<String, Integer> mapOfLemmas,
                                                 Page page) {
        List<Lemma> newLemmas = new ArrayList<>();
        for (Map.Entry<String, Integer> entry: mapOfLemmas.entrySet()) {

            Lemma newLemma = new Lemma();
            newLemma.setSite(page.getSite());
            newLemma.setLemma(entry.getKey());
            newLemma.setFrequency(1);
            newLemmas.add(newLemma);
        }
        return selectForUpdateOrInsertLemmas(newLemmas);
    }
}
