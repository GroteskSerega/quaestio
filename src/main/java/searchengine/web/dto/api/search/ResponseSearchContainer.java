package searchengine.web.dto.api.search;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseSearchContainer (
        boolean result,
        Integer count,
        List<DataPage> data
) {

}
