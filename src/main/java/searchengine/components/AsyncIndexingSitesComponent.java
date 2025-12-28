package searchengine.components;

import searchengine.model.Site;

import java.util.List;

public interface AsyncIndexingSitesComponent {
    void startAsyncProcessIndexingSites(List<Site> existingSites);
}
