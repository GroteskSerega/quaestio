package searchengine.component.lemma;

import searchengine.entity.Lemma;
import searchengine.entity.Page;

public interface LemmaProcessor {
    Lemma processLemmaWithLock(Page page, String lemmaName);
}
