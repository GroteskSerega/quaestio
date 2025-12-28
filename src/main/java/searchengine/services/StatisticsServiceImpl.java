package searchengine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import searchengine.components.PagesComponent;
import searchengine.components.SitesComponent;
import searchengine.dto.ResponseBody;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.Site;
import searchengine.model.SiteStatusType;
import searchengine.repositories.LemmasRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static searchengine.httpstatuscodes.HttpStatusCodes.HTTP_CODE_OK;
import static searchengine.logging.LoggingTemplates.TEMPLATE_SERVICE_API_REQUEST_STATISTICS;
import static searchengine.logging.LoggingTemplates.TEMPLATE_SERVICE_RESPONSE;
import static searchengine.messages.MessagesTemplates.TEMPLATE_API_STATISTICS_EMPTY_DB;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SitesComponent sitesComponent;
    private final PagesComponent pagesComponent;
    private final LemmasRepository lemmasRepository;

    @Override
    public ResponseEntity<ResponseBody> getStatistics() {
        log.info(TEMPLATE_SERVICE_API_REQUEST_STATISTICS);

        StatisticsResponse responseContainer = new StatisticsResponse();
        StatisticsData statDataContainer = new StatisticsData();
        TotalStatistics totalStat = new TotalStatistics();
        List<DetailedStatisticsItem> detailedList = new ArrayList<>();

        List<Site> sites = sitesComponent.getSitesFromDBMatchWithConfigAndOtherSitesFromConfig();

        for (Site site : sites) {
            if (site.getId() == null) {
                detailedList.add(createAndFillWithDefaultParams(site));
                continue;
            }
            detailedList.add(createAndFill(site));
        }

        totalStat.setSites(detailedList.size());

        totalStat.setIndexing(detailedList
                .stream()
                .anyMatch(item ->
                        item.getStatus().equals(SiteStatusType.INDEXING.toString())));

        totalStat.setPages(detailedList
                .stream()
                .map(DetailedStatisticsItem::getPages)
                .reduce(0, Integer::sum));

        totalStat.setLemmas(detailedList
                .stream()
                .map(DetailedStatisticsItem::getLemmas)
                .reduce(0, Integer::sum));

        statDataContainer.setTotal(totalStat);
        statDataContainer.setDetailed(detailedList);

        responseContainer.setStatistics(statDataContainer);
        responseContainer.setResult(true);

        log.info(TEMPLATE_SERVICE_RESPONSE,
                responseContainer,
                HttpStatusCode.valueOf(HTTP_CODE_OK));
        return new ResponseEntity<>(responseContainer, HttpStatusCode.valueOf(HTTP_CODE_OK));
    }

    private DetailedStatisticsItem createAndFillWithDefaultParams(Site siteFromConfig) {
        DetailedStatisticsItem item = new DetailedStatisticsItem();
        item.setName(siteFromConfig.getName());
        item.setUrl(siteFromConfig.getUrl());
        item.setStatus(SiteStatusType.FAILED.toString());
        item.setError(TEMPLATE_API_STATISTICS_EMPTY_DB);
        item.setStatusTime(LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
        item.setPages(0);
        item.setLemmas(0);
        return item;
    }

    private DetailedStatisticsItem createAndFill(Site siteFromDB) {
        DetailedStatisticsItem item = new DetailedStatisticsItem();
        item.setName(siteFromDB.getName());
        item.setUrl(siteFromDB.getUrl());
        item.setStatus(siteFromDB.getStatus().toString());
        item.setError(siteFromDB.getLastError());
        item.setStatusTime(siteFromDB.getStatusTime()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli());
        int somePages = pagesComponent.countAllBySiteId(siteFromDB.getId());
        int someLemmas = lemmasRepository.countAllBySiteId(siteFromDB.getId());
        item.setPages(somePages);
        item.setLemmas(someLemmas);
        return item;
    }
}
