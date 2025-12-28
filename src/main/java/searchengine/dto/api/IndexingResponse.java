package searchengine.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import searchengine.dto.ResponseBody;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndexingResponse implements ResponseBody {
    private Boolean result;
    private String error;
}
