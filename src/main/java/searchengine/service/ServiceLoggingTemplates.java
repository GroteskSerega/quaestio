package searchengine.service;

public class ServiceLoggingTemplates {

    public static final String TEMPLATE_SERVICE_API_REQUEST_START_INDEXING =
            "Request: Get /api/startIndexing";
    public static final String TEMPLATE_SERVICE_API_REQUEST_STOP_INDEXING =
            "Request: Get /api/stopIndexing";
    public static final String TEMPLATE_SERVICE_API_REQUEST_INDEX_PAGE =
            "Request: Post /api/indexPage. Form param url: {}";
    public static final String TEMPLATE_SERVICE_API_REQUEST_STATISTICS =
            "Request: Get /api/statistics";
    public static final String TEMPLATE_SERVICE_API_REQUEST_SEARCH =
            "Request: Get /api/search. query: {}, offset: {}, limit: {}, site: {}";

    public static final String TEMPLATE_ENGINE_STARTED_PROCESSING_SITE =
            "Started processing site {}";
    public static final String TEMPLATE_ENGINE_FINISHED_PROCESSING_SITE =
            "Finished processing site {}";
    public static final String TEMPLATE_ENGINE_STARTED_PROCESSING_URL =
            "Started processing url {}";
    public static final String TEMPLATE_ENGINE_FINISHED_PROCESSING_URL =
            "Finished processing url {}";
    public static final String TEMPLATE_SEARCH_CALCULATE_PERCENT_FOR_LEMMA =
            "Lemma {}, count of pages {}, count of lemma on pages-indexes {}, percent {}";
    public static final String TEMPLATE_SEARCH_COLLECTED_LEMMAS =
            "Collected lemmas {}";
    public static final String TEMPLATE_SEARCH_COLLECTED_RARE_LEMMAS =
            "Rare lemmas {}";
    public static final String TEMPLATE_SEARCH_CALCULATED_PAGE_RELEVANCE =
            "DataPage id: {}, lemmas - Rank: {}, absolute relevance: {}, relative relevance: {}";
}
