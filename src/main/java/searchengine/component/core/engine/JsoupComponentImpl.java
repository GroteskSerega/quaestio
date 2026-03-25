package searchengine.component.core.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import searchengine.config.JsoupConfig;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static searchengine.component.ComponentLoggingTemplates.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class JsoupComponentImpl implements JsoupComponent {

    private static final int TIMEOUT_MS = 3000;
    private static final String LINKS_CSS_SELECTOR = "a[href]";
    private static final String NAME_ITEM_ATTR_LINK = "href";

    // TODO! Warning! If string contains spec symbols - the exception can will be
    private static final String TEMPLATE_CSS_SELECTOR = "body *:matches((?i)%s)";
    private static final int AVAILABLE_LENGTH_FOR_SNIPPET = 100;
    private static final String TEMPLATE_REGEX_FOR_REPLACE_LEMMA_WITH_BOLD = "(?i)%s";
    private static final String TEMPLATE_TAG_BOLD = "<b>%s</b>";

    private final JsoupConfig jsoupConfig;

    @Override
    public Optional<Connection.Response> getResponse(String link) {
        Connection.Response response = null;

        try {
            response = Jsoup.connect(link)
                    .timeout(TIMEOUT_MS)
                    .userAgent(jsoupConfig.getUserAgent())
                    .referrer(jsoupConfig.getReferrer())
                    .execute();
        } catch (IOException e) {
            log.error(TEMPLATE_JSOUP_ERROR_BY_LINK,
                    link,
                    e.getMessage());
        }

        if (response == null) {
            return Optional.empty();
        }

        return Optional.of(response);
    }

    public Optional<Document> getDocument(String link, Connection.Response response) {
        Document document;
        try {
            document = response.parse();
        } catch (IOException e) {
            log.error(TEMPLATE_JSOUP_ERROR_BY_PARSE_LINK,
                    link,
                    e.getMessage());
            return Optional.empty();
        }

        log.info(TEMPLATE_JSOUP_LINK_AND_HTTP_RESULT_CODE,
                link,
                response.statusCode());

        return Optional.of(document);
    }

    public Set<String> getLinksFromDocument(Document doc) {
        Set<String> linkSet = new HashSet<>();

        Elements linksItems = doc.select(LINKS_CSS_SELECTOR);

        for (Element item : linksItems) {
            linkSet.add(item.attr(NAME_ITEM_ATTR_LINK));
        }

        return linkSet;
    }

    @Override
    public String getTextFromDocument(Document document) {
        return document.body().text();
    }

    @Override
    public Document getDocumentFromText(String content) {
        return Jsoup.parse(content);
    }

    @Override
    public String getTitleFromDocument(Document document) {
        return document.title();
    }

    @Override
    public String getSnippetFromDocument(Document document,
                                         String query) {
        String[] words = query.split(" ");
        Elements elements = null;
        String result = "";

        for (String word : words) {
            log.info(TEMPLATE_JSOUP_SNIPPET_ELEMENT_BY_WORD_FROM_QUERY,
                    word,
                    elements);

            elements = document.select(TEMPLATE_CSS_SELECTOR.formatted(word));

            if (!elements.isEmpty()) {
                result = elements.get(0).text();
                break;
            }
        }

        if (result.isEmpty()) {
            return "";
        }

        if (result.length() > AVAILABLE_LENGTH_FOR_SNIPPET) {
            result = result.substring(0, AVAILABLE_LENGTH_FOR_SNIPPET).concat("...");
        }

        for (String word : words) {
            result = result.replaceAll(TEMPLATE_REGEX_FOR_REPLACE_LEMMA_WITH_BOLD.formatted(word),
                    TEMPLATE_TAG_BOLD.formatted(word));
        }

        return result;
    }
}
