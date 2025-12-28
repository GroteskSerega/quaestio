package searchengine.components;

import java.util.Map;

public interface LuceneMorphologyComponent {
    enum Lang {
        RU,
        EN
    }
    String getTextFromHTML(String htmlBody);
    Map<String, Integer> calculateLemmas(String text, Lang lang);
}
