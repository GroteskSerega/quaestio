package searchengine.web.dto.api.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StatisticsData (
        TotalStatistics total,
        List<DetailedStatisticsItem> detailed
) {
}
