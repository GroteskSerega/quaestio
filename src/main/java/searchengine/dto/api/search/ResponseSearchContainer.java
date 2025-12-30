package searchengine.dto.api.search;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import searchengine.dto.ResponseBody;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseSearchContainer implements ResponseBody {
    private boolean result;
    private Integer count;
    private List<DataPage> data;
}
