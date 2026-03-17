package searchengine.web.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record IndexingResponse (
        Boolean result,
        String error
) {

}
