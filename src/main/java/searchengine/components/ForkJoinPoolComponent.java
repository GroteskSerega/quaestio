package searchengine.components;

import searchengine.model.Site;

public interface ForkJoinPoolComponent {
    void startIndexSite(Site site);
}
