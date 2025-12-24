package searchengine.components;

import searchengine.model.Site;

import java.util.List;

public interface AsyncIndexingComponent {
    void startAsyncProcessIndexingSites(List<Site> existingSites);
}
