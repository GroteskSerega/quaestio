package searchengine.components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.morphology.LuceneMorphology;
import org.springframework.stereotype.Component;
import searchengine.config.LuceneMorphConfig;
import searchengine.core.utility.JsoupUtility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Component
public class LuceneMorphologyComponentImpl implements LuceneMorphologyComponent {

    private final LuceneMorphConfig luceneMorphConfig;

    private final String REGEX_RUSSIAN_WORDS = "[а-яА-Я]+";
    private final String REGEX_ENGLISH_WORDS = "[a-zA-Z]+";
    private final String REGEX_SERVICE_PARTS_OF_SPEECH_RUSSIAN = ".*(ПРЕДЛ|МЕЖД|СОЮЗ).*";
    private final String REGEX_SERVICE_PARTS_OF_SPEECH_ENGLISH = ".*(ARTICLE|PREP|ADJECTIVE|PN_ADJ).*";

    @Override
    public String getTextFromHTML(String htmlBody) {
        return JsoupUtility.getTextFromHTML(htmlBody);
    }

    @Override
    public Map<String, Integer> calculateLemmas(String text, Lang lang) {

        Map<String, Integer> lemmasCount = calculateWords(text, lang);
        log.info("Collect words: {}",
                lemmasCount.size());
        return lemmasCount;
    }

    private Map<String, Integer> calculateWords(String text,
                                                Lang lang) {
        Map<String, Integer> lemmasCount = new HashMap<>();

        LuceneMorphology luceneMorph = getLuceneMorphologyByLang(lang);
        String useRegex = getUseRegexByLang(lang);
        String useRegexServicePartsOfSpeech = getUseRegexServicePartsOfSpeech(lang);

        Pattern pattern = Pattern.compile(useRegex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String word = matcher.group().toLowerCase();

            if (luceneMorph.checkString(word)) {
                List<String> morphInfo = luceneMorph.getMorphInfo(word);
                boolean skipWord = validateWordByServicePartsOfSpeech(morphInfo,
                        useRegexServicePartsOfSpeech);
                if (skipWord) {
                    continue;
                }
                List<String> normalForms = luceneMorph.getNormalForms(word);
                if (!normalForms.isEmpty()) {
                    String form = normalForms.get(0);
                    lemmasCount.put(form,
                            lemmasCount.get(form) != null ? lemmasCount.get(form) + 1 : 1);
                }
            }
        }
        return lemmasCount;
    }

    private LuceneMorphology getLuceneMorphologyByLang(Lang lang) {
        return switch (lang) {
            case RU -> luceneMorphConfig.getLuceneMorphRussian();
            case EN -> luceneMorphConfig.getLuceneMorphEnglish();
        };
    }

    private String getUseRegexByLang(Lang lang) {
        return switch(lang) {
            case RU -> REGEX_RUSSIAN_WORDS;
            case EN -> REGEX_ENGLISH_WORDS;
        };
    }

    private String getUseRegexServicePartsOfSpeech(Lang lang) {
        return switch(lang) {
            case RU -> REGEX_SERVICE_PARTS_OF_SPEECH_RUSSIAN;
            case EN -> REGEX_SERVICE_PARTS_OF_SPEECH_ENGLISH;
        };
    }

    private boolean validateWordByServicePartsOfSpeech(List<String> morphInfo,
                                                       String useRegexServicePartsOfSpeech) {
        for (String morph : morphInfo) {
            if (morph.matches(useRegexServicePartsOfSpeech)) {
                return true;
            }
        }
        return false;
    }
}
