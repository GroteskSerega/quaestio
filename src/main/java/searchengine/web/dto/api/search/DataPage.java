package searchengine.web.dto.api.search;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DataPage (
        String site,
        String siteName,
        String uri,
        String title,
        String snippet,
        Float relevance
) {

}
