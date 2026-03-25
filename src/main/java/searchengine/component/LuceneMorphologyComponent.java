package searchengine.component;

import java.util.Map;

public interface LuceneMorphologyComponent {
    Map<String, Integer> calculateLemmas(String text);
}
