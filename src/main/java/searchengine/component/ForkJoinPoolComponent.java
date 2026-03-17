package searchengine.component;

import searchengine.entity.Site;

public interface ForkJoinPoolComponent {
    void startIndexSite(Site site);
}
