package searchengine.component;

public class ComponentLoggingTemplates {

    public static final String TEMPLATE_REPOSITORY_SITES_FOUND =
            "Found sites in DB: {}";
    public static final String TEMPLATE_REPOSITORY_SITES_FOUND_IN_DB_AND_CONFIG =
            "Found sites in DB and config: {}";
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
    public static final String TEMPLATE_REPOSITORY_PAGES_FOUND_BY_IDS =
            "Found pages in DB: {} by ids: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_FOUND_BY_SITE_ID_AND_URI =
            "Found page by siteId:{} and uri:{} in DB: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_FIRST_FOUND_BY_SITE_ID =
            "Found first page by siteId:{} in DB: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_TRY_TO_SAVE =
            "Try to save pages in DB: siteUrl: {} and path: {} ";
    public static final String TEMPLATE_REPOSITORY_PAGES_SAVED =
            "Saved pages in DB pageId: {}, siteUrl {} and path: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_TRY_TO_DELETE_BY_SITES_IDS =
            "Try to delete pages in DB by siteId: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_TRY_TO_DELETE_BY_SITE_ID_AND_PATH =
            "Try to delete pages in DB by siteId: {} and path: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_DELETED_BY_SITES_IDS =
            "Deleted pages in DB by siteId: {}";
    public static final String TEMPLATE_REPOSITORY_PAGES_DELETED_BY_SITE_ID_AND_PATH =
            "Deleted pages in DB by siteId: {} and path: {}";

    public static final String TEMPLATE_REPOSITORY_LEMMAS_ID_FOUND_BY_SITE_ID_AND_LEMMA =
            "Found lemmas in DB: {} by siteId: {} and lemma: {}";
    public static final String TEMPLATE_REPOSITORY_LEMMAS_FOUND_BY_SITE_ID_AND_LEMMAS =
            "Found lemmas in DB: {} by sitesId: {} and lemmas: {}";
    public static final String TEMPLATE_REPOSITORY_LEMMAS_TRY_TO_SAVE =
            "Try to save lemmas in DB: {}";
    public static final String TEMPLATE_REPOSITORY_LEMMAS_SAVED =
            "Saved lemmas: {}";
    public static final String TEMPLATE_REPOSITORY_LEMMAS_TRY_TO_DELETE_BY_SITE_ID =
            "Try to delete lemmas in DB by siteId: {}";
    public static final String TEMPLATE_REPOSITORY_LEMMAS_DELETED_BY_SITE_ID =
            "Deleted lemmas in DB by siteId: {}";

    public static final String TEMPLATE_REPOSITORY_INDEXES_FOUND_BY_LEMMA_ID_AND_PAGE_ID =
            "Found index by lemmaId: {} and pageId:{} in DB: {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_FOUND_PAGE_IDS_BY_SITE_IDS_AND_LEMMA_ID =
            "Found index pageIds in DB: {} by sitesIds: {} and lemmaId: {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_COUNT_ROWS_BY_LEMMAS_ID =
            "Count indexes in DB: {} by lemmaId: {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_TRY_TO_SAVE =
            "Try to save indexes in DB: {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_SAVED =
            "Saved indexes in DB: {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_TRY_TO_DELETE_BY_PAGE_ID =
            "Try to delete indexes in DB by pageId: {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_TRY_TO_DELETE_BY_SITES_IDS =
            "Try to delete indexes in DB by sites ids: {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_DELETED_BY_PAGE_ID =
            "Deleted indexes in DB by pageId: {}";
    public static final String TEMPLATE_REPOSITORY_INDEXES_DELETED_BY_SITES_IDS =
            "Deleted indexes in DB by sites ids: {}";

    public static final String TEMPLATE_FORK_JOIN_POOL_USING =
            "CustomForkJoinPool using {}.";
    public static final String TEMPLATE_FORK_JOIN_POOL_COMPLETED =
            "CustomForkJoinPool using is completed. {}";
    public static final String TEMPLATE_FORK_JOIN_POOL_GET_STATISTIC = """
            
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

    public static final String TEMPLATE_JSOUP_ERROR_BY_LINK =
            "Occurred error by link: {}, {}";
    public static final String TEMPLATE_JSOUP_ERROR_BY_PARSE_LINK =
            "Occurred error by parse response: {}, {}";
    public static final String TEMPLATE_JSOUP_LINK_AND_HTTP_RESULT_CODE =
            "\nLink: {}. HTTP Result code: {}";
    public static final String TEMPLATE_JSOUP_SNIPPET_ELEMENT_BY_WORD_FROM_QUERY =
            "word: {}, element: {}";
}
