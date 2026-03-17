package searchengine.web.dto.api.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TotalStatistics (
        int sites,
        int pages,
        int lemmas,
        boolean indexing
) {

}
