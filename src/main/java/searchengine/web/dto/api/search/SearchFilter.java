package searchengine.web.dto.api.search;

public record SearchFilter (
        String query,
        Integer offset,
        Integer limit,
        String site
) {

}
