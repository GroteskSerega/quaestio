package searchengine.web.dto.api.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StatisticsResponse (
        boolean result,
        StatisticsData statistics
) {

}
