package searchengine.service;

import searchengine.web.dto.api.search.ResponseSearchContainer;

public interface SearchService {
    ResponseSearchContainer search(String query, Integer offset, Integer limit, String selectedSite);
}
