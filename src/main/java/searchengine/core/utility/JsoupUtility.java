package searchengine.core.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.config.JsoupConfig;
import searchengine.model.Page;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static searchengine.logging.LoggingTemplates.*;

@Slf4j
@RequiredArgsConstructor
public class JsoupUtility {

    private static final int TIMEOUT = 3000;
    private static final String XPATH_LINKS = "//a";


    private static final String NAME_ITEM_ATTR_LINK = "href";

    public static Page createPageByLink(String link,
                                        JsoupConfig jsoupConfig) {
        Page pageCandidate = new Page();
        Connection.Response response = null;

        try {
            response = execute(link, jsoupConfig);
        } catch (IOException e) {
            log.error(TEMPLATE_JSOUP_ERROR_BY_LINK,
                    link,
                    e.getMessage());
        }

        if (response == null) {
            return null;
        }

        pageCandidate.setCode(response.statusCode());

        Document doc = null;
        try {
            doc = getDocument(link, response);
        } catch (IOException e) {
            log.error(TEMPLATE_JSOUP_ERROR_BY_PARSE_LINK,
                    link,
                    e.getMessage());
        }

        if (doc == null) {
            return null;
        }

        pageCandidate.setContent(doc.outerHtml());
        return pageCandidate;
    }

    public static Set<String> getLinksFromContent(String content) {
        Document doc = Jsoup.parse(content);
        return getLinksFromDOM(doc);
    }

    private static Connection.Response execute(String link,
                                               JsoupConfig jsoupConfig) throws IOException {
        Connection connect = Jsoup.connect(link);
        connect.timeout(TIMEOUT);
        connect.userAgent(jsoupConfig.getUserAgent())
                .referrer(jsoupConfig.getReferrer());
        return connect.execute();
    }

    public static String getTextFromHTML(String htmlBody) {
        Document doc = Jsoup.parse(htmlBody);
        return doc.body().text();
    }


    private static Document getDocument(String link,
                                        Connection.Response response) throws IOException {
        log.info(TEMPLATE_JSOUP_LINK_AND_HTTP_RESULT_CODE,
                link,
                response.statusCode());

        return response.parse();
    }

    private static Set<String> getLinksFromDOM(Document doc) {
        Set<String> linkSet = new HashSet<>();

        Elements linksItems = doc.selectXpath(XPATH_LINKS);
        for (Element item : linksItems) {
            String link = item.attr(NAME_ITEM_ATTR_LINK);
            linkSet.add(link);
        }
        return linkSet;
    }
}
