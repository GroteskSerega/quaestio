package searchengine.component.core.engine;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;

import java.util.Optional;
import java.util.Set;

public interface JsoupComponent {

    Optional<Connection.Response> getResponse(String link);
    Optional<Document> getDocument(String link, Connection.Response response);
    Set<String> getLinksFromDocument(Document document);
    String getTextFromDocument(Document document);
    Document getDocumentFromText(String content);
    String getTitleFromDocument(Document document);
    String getSnippetFromDocument(Document document, String query);
}
