package searchengine.logging;

public class LoggingTemplates {
    public static final String TEMPLATE_CONFIG_APP_CONFIG =
            "App Config: {}";
    public static final String TEMPLATE_CONFIG_SITES_CONFIG =
            "Sites Config: {}";
    public static final String TEMPLATE_CONFIG_SITES_IS_EMPTY =
            "!!!!!Sites Config is empty. Application will stop.";
    public static final String TEMPLATE_CONFIG_JSOUP_HEADERS_CONFIG =
            "Jsoup config: {}";

    public static final String TEMPLATE_SERVICE_API =
            "Request: method {}, uri {}";
    public static final String TEMPLATE_SERVICE_API_REQUEST_START_INDEXING =
            "Request: Get /api/startIndexing";
    public static final String TEMPLATE_SERVICE_API_REQUEST_STOP_INDEXING =
            "Request: Get /api/stopIndexing";
    public static final String TEMPLATE_SERVICE_API_REQUEST_INDEX_PAGE =
            "Request: Post /api/indexPage. Form param url: {}";
    public static final String TEMPLATE_SERVICE_API_REQUEST_STATISTICS =
            "Request: Get /api/statistics";
    public static final String TEMPLATE_SERVICE_RESPONSE = "Response: {}\n{}";
    public static final String TEMPLATE_SERVICE_RESPONSE_CODE = "Response code: {}";

    public static final String TEMPLATE_REPOSITORY_SITES_FOUNDED =
            "Founded sites in DB: {}";
    public static final String TEMPLATE_REPOSITORY_SITES_FOUNDED_IN_DB_AND_CONFIG =
            "Founded sites in DB and config: {}";
    public static final String TEMPLATE_REPOSITORY_SITES_TRY_TO_SAVE =
            "Try to save sites in DB: {}";
    public static final String TEMPLATE_REPOSITORY_SITES_SAVED =
            "Saved sites in DB: {}";
    public static final String TEMPLATE_REPOSITORY_SITES_TRY_TO_DELETE =
            "Try to delete sites in DB: {}";
    public static final String TEMPLATE_REPOSITORY_SITES_DELETED =
            "Deleted sites in DB: {}";

    public static final String TEMPLATE_REPOSITORY_PAGES_COUNT_BY_SITE_ID =
            "Count pages in DB: {} by siteId {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_FOUNDED_LIST_IDS_BY_SITE_ID =
            "Founded pages ids in DB: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_FOUNDED =
            "Founded pages in DB: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_FOUNDED_BY_SITE_ID_AND_URI =
            "Founded page by siteId:{} and uri:{} in DB: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_FIRST_FOUNDED_BY_SITE_ID =
            "Founded first page by siteId:{} in DB: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_TRY_TO_SAVE =
            "Try to save pages in DB: siteId: {} and path: {} ";
    public static final String TEMPLATE_REPOSITORY_PAGES_SAVED =
            "Saved pages in DB pageId: {}, siteId {} and path: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_TRY_TO_DELETE =
            "Try to delete pages in DB: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_TRY_TO_DELETE_BY_SITE_ID =
            "Try to delete pages in DB by siteId: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_TRY_TO_DELETE_BY_SITE_ID_AND_PATH =
            "Try to delete pages in DB by siteId: {} and path: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_DELETED =
            "Deleted pages in DB: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_DELETED_BY_SITE_ID =
            "Deleted pages in DB by siteId: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_DELETED_BY_SITE_ID_AND_PATH =
            "Deleted pages in DB by siteId: {} and path: {}";

    public static final String TEMPLATE_REPOSITORY_LEMMAS_COUNT_BY_SITE_ID =
            "Count lemmas in DB: {} by siteId {}";
    public static final String TEMPLATE_REPOSITORY_LEMMAS_FOUNDED = "";
    public static final String TEMPLATE_REPOSITORY_LEMMAS_TRY_TO_SAVE =
            "Try to save lemmas in DB: {}";
    public static final String TEMPLATE_REPOSITORY_LEMMAS_SAVED =
            "Saved lemmas in DB: {}";
    public static final String TEMPLATE_REPOSITORY_LEMMAS_TRY_TO_DELETE_BY_SITE_ID =
            "Try to delete lemmas in DB by siteId: {}";
    public static final String TEMPLATE_REPOSITORY_LEMMAS_TRY_TO_DELETE_BY_IDS =
            "Try to delete lemmas in DB by ids: {}";
    public static final String TEMPLATE_REPOSITORY_LEMMAS_DELETED_BY_SITE_ID =
            "Deleted lemmas in DB by siteId: {}";
    public static final String TEMPLATE_REPOSITORY_LEMMAS_DELETED_BY_IDS =
            "Deleted lemmas in DB by ids: {}";

    public static final String TEMPLATE_REPOSITORY_INDEXES_COUNT_BY_SITE_ID =
            "Count indexes ids in DB: {} by siteId {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_FOUNDED =
            "Founded indexes in DB: {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_TRY_TO_SAVE =
            "Try to save indexes in DB: {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_SAVED =
            "Saved indexes in DB: {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_TRY_TO_DELETE_BY_PAGE_ID =
            "Try to delete indexes in DB by pageId: {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_TRY_TO_DELETE_BY_PAGE_ID_AND_LEMMA_ID =
            "Try to delete indexes in DB by pageId: {} and lemmaId: {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_DELETED_BY_PAGE_ID =
            "Deleted indexes in DB by pageId: {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_DELETED_BY_PAGE_ID_AND_LEMMA_ID =
            "Deleted indexes in DB by pageId: {} and lemmaId: {}";

    public static final String TEMPLATE_ENGINE_STARTED_PROCESSING_SITE =
            "Started processing site {}";
    public static final String TEMPLATE_ENGINE_FINISHED_PROCESSING_SITE =
            "Finished processing site {}";
    public static final String TEMPLATE_ENGINE_STARTED_PROCESSING_URL =
            "Started processing url {}";
    public static final String TEMPLATE_ENGINE_FINISHED_PROCESSING_URL =
            "Finished processing url {}";

    public static final String TEMPLATE_CONFIG_FORK_JOIN_POOL_CREATE =
            "CustomForkJoinPool created. {}";
    public static final String TEMPLATE_CONFIG_FORK_JOIN_POOL_SHUTDOWN =
            "CustomForkJoinPool shut down. {}";

    public static final String TEMPLATE_RECURSIVE_TASK_MANAGER_ENGINE_LINK =
            "\nLink: {}";

    public static final String TEMPLATE_JSOUP_ERROR_BY_LINK =
            "Occurred error by link: {}, {}";
    public static final String TEMPLATE_JSOUP_ERROR_BY_PARSE_LINK =
            "Occurred error by parse response: {}, {}";
    public static final String TEMPLATE_JSOUP_LINK_AND_HTTP_RESULT_CODE =
            "\nLink: {}. HTTP Result code: {}";
    public static final String TEMPLATE_JSOUP_NON_200_HTTP_CODE_LINK_AND_HTTP_RESULT_CODE =
            "\nLink: {}. Returned non 200 response code: {}";

    public static final String TEMPLATE_COMPONENT_FORK_JOIN_POOL_GET_STATISTIC = """
            ******************************************
            Site: {}
            TaskManager isDone: {}
            TaskManager isCancelled: {}
            TaskManager isCompletedAbnormally: {}
            TaskManager isCompletedNormally: {}
            TaskManager cancelled: {}
            Parallelism: {}
            Active Threads: {}
            Task Count: {}
            Steal Count: {}
            ******************************************""";
}
