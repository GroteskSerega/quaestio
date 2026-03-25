package searchengine.component.lemma;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.entity.Lemma;
import searchengine.entity.Page;
import searchengine.repository.LemmaRepository;

@Component
@RequiredArgsConstructor
public class LemmaProcessorImpl implements LemmaProcessor {

    private static final int INITIAL_VALUE_FREQUENCY = 1;

    private final LemmaRepository lemmaRepository;

    @Value("${app.maxRetriesForDeadLock}")
    private int maxRetriesForDeadLock;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Lemma processLemmaWithLock(Page page, String lemmaName) {
        for (int i = 0; i < maxRetriesForDeadLock; i++) {
            try {
                return executeUpsert(page, lemmaName);
            } catch (Exception e) {
                if (i == maxRetriesForDeadLock - 1) throw e;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {

                }
            }
        }

        return null;
    }

    private Lemma executeUpsert(Page page, String lemmaName) {
        return lemmaRepository.findBySiteIdAndLemmaWithLock(page.getSite().getId(),
                        lemmaName)
                .map(existing -> {
                    existing.setFrequency(existing.getFrequency() + INITIAL_VALUE_FREQUENCY);
                    return existing;
                })
                .orElseGet(() -> {
                    Lemma newLemma = new Lemma();
                    newLemma.setSite(page.getSite());
                    newLemma.setLemma(lemmaName);
                    newLemma.setFrequency(INITIAL_VALUE_FREQUENCY);
                    return lemmaRepository.saveAndFlush(newLemma);
                });
    }
}
