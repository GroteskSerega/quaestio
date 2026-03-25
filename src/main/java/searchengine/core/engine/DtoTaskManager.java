package searchengine.core.engine;

import searchengine.component.*;
import searchengine.component.core.engine.JsoupComponent;
import searchengine.entity.Site;

public record DtoTaskManager(
        String link,
        String vertexLink,
        Site site,
        SitesComponent sitesComponent,
        PagesComponent pagesComponent,
        LemmasComponent lemmasComponent,
        IndexesComponent indexesComponent,
        LuceneMorphologyComponent luceneMorphologyComponent,
        JsoupComponent jsoupComponent
) {
}
