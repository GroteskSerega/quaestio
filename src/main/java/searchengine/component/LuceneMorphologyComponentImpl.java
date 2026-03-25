package searchengine.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.morphology.LuceneMorphology;
import org.springframework.stereotype.Component;
import searchengine.component.lang.Lang;
import searchengine.config.LuceneMorphConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * For add new language - need added library to project,
 * add language to package searchengine.component.lang,
 * add symbols to regex REGEX_UNIVERSAL_WORDS,
 * add new Pattern,
 * modify methods getLuceneMorphologyByLang() and getServicePattern()
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class LuceneMorphologyComponentImpl implements LuceneMorphologyComponent {

    private final LuceneMorphConfig luceneMorphConfig;

    private static final String REGEX_UNIVERSAL_WORDS = "[а-яА-Яa-zA-Z]+";
    private static final Pattern UNIVERSAL_PATTERN = Pattern.compile(REGEX_UNIVERSAL_WORDS);

    private static final String REGEX_SERVICE_PARTS_OF_SPEECH_RUSSIAN = ".*(ПРЕДЛ|МЕЖД|СОЮЗ).*";
    private static final String REGEX_SERVICE_PARTS_OF_SPEECH_ENGLISH = ".*(ARTICLE|PREP|ADJECTIVE|PN_ADJ).*";

    private static final Pattern RU_SERVICE_PARTS = Pattern.compile(REGEX_SERVICE_PARTS_OF_SPEECH_RUSSIAN);
    private static final Pattern EN_SERVICE_PARTS = Pattern.compile(REGEX_SERVICE_PARTS_OF_SPEECH_ENGLISH);

    @Override
    public Map<String, Integer> calculateLemmas(String text) {
        Map<String, Integer> lemmasCount = calculateWords(text);
        log.info("Collect words: {}",
                lemmasCount.size());
        return lemmasCount;
    }

    private Map<String, Integer> calculateWords(String text) {
        Map<String, Integer> lemmasCount = new HashMap<>();

        Matcher matcher = UNIVERSAL_PATTERN.matcher(text);

        while (matcher.find()) {
            String word = matcher.group().toLowerCase();

            Lang detectedLang = null;

            for (Lang lang : Lang.values()) {
                if (lang.matches(word)) {
                    detectedLang = lang;
                    break;
                }
            }

            if (detectedLang == null) {
                continue;
            }

            LuceneMorphology luceneMorph =
                    getLuceneMorphologyByLang(detectedLang);

            Pattern servicePattern =
                    getServicePattern(detectedLang);

            if (luceneMorph.checkString(word)) {
                processWord(word, luceneMorph, servicePattern, lemmasCount);
            }
        }
        return lemmasCount;
    }

    private LuceneMorphology getLuceneMorphologyByLang(Lang detectedLang) {
        return switch (detectedLang) {
            case RU -> luceneMorphConfig.getLuceneMorphRussian();
            case EN -> luceneMorphConfig.getLuceneMorphEnglish();
        };
    }

    private Pattern getServicePattern(Lang detectedLang) {
        return switch (detectedLang) {
            case RU -> RU_SERVICE_PARTS;
            case EN -> EN_SERVICE_PARTS;
        };
    }

    private void processWord(String word,
                             LuceneMorphology luceneMorph,
                             Pattern servicePattern,
                             Map<String, Integer> lemmasCount) {
        List<String> morphInfo = luceneMorph.getMorphInfo(word);

        boolean skipWord = morphInfo
                .stream()
                .anyMatch(morph ->
                        servicePattern.matcher(morph)
                                .matches());

        if (skipWord) {
            return;
        }

        List<String> normalForms = luceneMorph.getNormalForms(word);

        if (!normalForms.isEmpty()) {
            lemmasCount.merge(normalForms.get(0), 1, Integer::sum);
        }
    }
}
