package searchengine.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Getter
@Setter
@ToString
@Configuration
public class LuceneMorphConfig {
    private LuceneMorphology luceneMorphRussian;
    private LuceneMorphology luceneMorphEnglish;

    @PostConstruct
    public void initLuceneMorph() throws IOException {
        luceneMorphRussian = new RussianLuceneMorphology();
        luceneMorphEnglish = new EnglishLuceneMorphology();
    }
}
