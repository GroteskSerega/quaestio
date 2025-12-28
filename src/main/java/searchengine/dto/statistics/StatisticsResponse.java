package searchengine.dto.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import searchengine.dto.ResponseBody;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticsResponse implements ResponseBody {
    private boolean result;
    private StatisticsData statistics;
}
