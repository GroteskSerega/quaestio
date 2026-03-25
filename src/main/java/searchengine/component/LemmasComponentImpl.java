package searchengine.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import searchengine.component.lemma.LemmaProcessor;
import searchengine.entity.Lemma;
import searchengine.entity.Page;
import searchengine.repository.LemmaRepository;

import java.util.*;

import static searchengine.component.ComponentLoggingTemplates.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class LemmasComponentImpl implements LemmasComponent {

    private final LemmaProcessor lemmaProcessor;
    private final LemmaRepository lemmaRepository;


    @Override
    @Transactional
    public void deleteBySiteIds(List<Integer> siteIds) {
        log.info(TEMPLATE_REPOSITORY_LEMMAS_TRY_TO_DELETE_BY_SITE_ID,
                Arrays.toString(siteIds.toArray()));
        lemmaRepository.deleteBySiteIds(siteIds);
        log.info(TEMPLATE_REPOSITORY_LEMMAS_DELETED_BY_SITE_ID,
                Arrays.toString(siteIds.toArray()));
    }

    @Override
    @Transactional
    public List<Lemma> saveLemmasToDB(List<Lemma> lemmas) {
        log.info(TEMPLATE_REPOSITORY_LEMMAS_TRY_TO_SAVE, lemmas.size());
        List<Lemma> lemmaList = lemmaRepository.saveAll(lemmas);
//        lemmaIterable.forEach(lemma ->
//                log.info(TEMPLATE_REPOSITORY_LEMMAS_SAVED, lemma));
        log.info(TEMPLATE_REPOSITORY_LEMMAS_SAVED, lemmas.size());
        return lemmaList;
    }

    @Override
//    @Transactional
    public List<Lemma> prepareAndSaveLemmas(Map<String, Integer> mapOfLemmas,
                                                 Page page) {
        log.info(TEMPLATE_REPOSITORY_LEMMAS_TRY_TO_SAVE,
                mapOfLemmas.size());

        List<Lemma> newLemmas = new ArrayList<>();

        List<String> sortedLemmaNames = new ArrayList<>(mapOfLemmas.keySet());
        Collections.sort(sortedLemmaNames);

        for (String lemmaName : sortedLemmaNames) {

            Lemma existedLemma =
                    lemmaProcessor.processLemmaWithLock(page, lemmaName);

            newLemmas.add(existedLemma);
        }

        log.info(TEMPLATE_REPOSITORY_LEMMAS_SAVED,
                newLemmas.size());
        return newLemmas;
    }

    @Override
    public List<Lemma> findAllBySiteIdInAndLemmaIn(List<Integer> sitesId, List<String> lemmas) {
        List<Lemma> lemmaList = lemmaRepository.findAllBySiteIdInAndLemmaIn(sitesId, lemmas);
        log.info(TEMPLATE_REPOSITORY_LEMMAS_FOUND_BY_SITE_ID_AND_LEMMAS,
                lemmaList,
                Arrays.toString(sitesId.toArray()),
                Arrays.toString(lemmas.toArray()));
        return lemmaList;
    }

    @Override
    public List<Integer> findAllIdBySiteIdInAndLemma(List<Integer> siteId, String lemma) {
        List<Integer> ids = lemmaRepository.findAllIdBySiteIdInAndLemma(siteId, lemma);
        log.info(TEMPLATE_REPOSITORY_LEMMAS_ID_FOUND_BY_SITE_ID_AND_LEMMA,
                Arrays.toString(ids.toArray()),
                Arrays.toString(siteId.toArray()),
                lemma);
        return ids;
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        lemmaRepository.deleteById(id);
    }
}
