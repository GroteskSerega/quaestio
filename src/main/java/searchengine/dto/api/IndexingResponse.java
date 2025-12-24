package searchengine.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import searchengine.dto.ResponseBody;

@NoArgsConstructor
@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndexingResponse implements ResponseBody {
    private Boolean result;
    private String error;
}
