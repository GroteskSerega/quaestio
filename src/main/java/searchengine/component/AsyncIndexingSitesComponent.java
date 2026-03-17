package searchengine.component;

import searchengine.entity.Site;

import java.util.List;

public interface AsyncIndexingSitesComponent {
    void startAsyncProcessIndexingSites(List<Site> existingSites);
}
