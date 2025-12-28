package searchengine.messages;

public class MessagesTemplates {
    public static final String TEMPLATE_API_INDEXING_ALREADY_STARTED =
            "Индексация уже запущена";
    public static final String TEMPLATE_API_INDEXING_NOT_STARTED =
            "Индексация не запущена";
    public static final String TEMPLATE_API_INDEXING_PAGE_NOT_RELATED_PAGE =
            "Данная страница находится за пределами сайтов, указанных в конфигурационном файле";
    public static final String TEMPLATE_API_STATISTICS_EMPTY_DB =
            "Отсутствует информация о сайте";

    public static final String TEMPLATE_REASON_FAILED_BY_USER =
            "Индексация остановлена пользователем";
    public static final String TEMPLATE_REASON_FAILED_SITE_CAN_NOT_BE_REACHED =
            "Ошибка индексации: главная страница сайта не доступна";
}
