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

// TODO SOME SPAGHETTI CODE WITH TaskManagerEngine class. NEED REFACTORING
@Slf4j
@RequiredArgsConstructor
public class JsoupUtility {

    private static final int TIMEOUT = 3000;
    private static final int HTTP_STATUS_CODE_FOR_PARSE_RESPONSE = 200;
    private static final String XPATH_LINKS = "//a";

    private static final String FIRST_CHAR_LINK_FOR_VALIDATE = "/";
    private static final String REGEX_VALID_LINKS = "^/.+";
    private static final String NAME_ITEM_ATTR_LINK = "href";
    private static final String TEMPLATE_REGEX_VERTEX_LINK = "%s.+";


    public static Set<String> getChildrenPageLinks(String link,
                                                   Page pageCandidate,
                                                   JsoupConfig jsoupConfig) {
        Connection.Response response = null;

        try {
            response = execute(link, jsoupConfig);
        } catch (IOException e) {
            log.error(TEMPLATE_JSOUP_ERROR_BY_LINK,
                    link,
                    e.getMessage());
        }

        if (response == null) {
            return new HashSet<>();
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
            return new HashSet<>();
        }

        pageCandidate.setContent(doc.outerHtml());
        return getLinksFromDOM(doc);
    }

    private static Connection.Response execute(String link,
                                               JsoupConfig jsoupConfig) throws IOException {
        Connection connect = Jsoup.connect(link);
        connect.timeout(TIMEOUT);
        connect.userAgent(jsoupConfig.getUserAgent()).referrer(jsoupConfig.getReferrer());
        return connect.execute();
    }

    private static Document getDocument(String link,
                                        Connection.Response response) throws IOException {
        log.info(TEMPLATE_JSOUP_LINK_AND_HTTP_RESULT_CODE,
                link,
                response.statusCode());

        if (response.statusCode() == HTTP_STATUS_CODE_FOR_PARSE_RESPONSE) {
            return response.parse();
        }

        log.warn(TEMPLATE_JSOUP_NON_200_HTTP_CODE_LINK_AND_HTTP_RESULT_CODE,
                link,
                response.statusCode());

        return null;
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

    public static Set<String> filteringLinksByHostAndCreateFullLink(Set<String> links,
                                                                    String vertexLink) {
        Set<String> filteredLinks = new HashSet<>();

        String regexCheckLinkByVertexLink =
                String.format(TEMPLATE_REGEX_VERTEX_LINK, vertexLink);

        for (String link: links) {
            boolean isValidLink = link.matches(REGEX_VALID_LINKS);
            boolean isLinkWithVertexLink = link.matches(regexCheckLinkByVertexLink);

            if (isLinkWithVertexLink) {
                filteredLinks.add(link);
            }

            if (isValidLink) {
                if (link.startsWith(FIRST_CHAR_LINK_FOR_VALIDATE)) {
                    link = vertexLink.concat(link.substring(1));
                }
                filteredLinks.add(link);
            }
        }
        return filteredLinks;
    }
}
