package searchengine.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.component.PagesComponent;
import searchengine.component.SitesComponent;
import searchengine.web.dto.api.statistics.DetailedStatisticsItem;
import searchengine.web.dto.api.statistics.StatisticsData;
import searchengine.web.dto.api.statistics.StatisticsResponse;
import searchengine.web.dto.api.statistics.TotalStatistics;
import searchengine.entity.Site;
import searchengine.entity.SiteStatusType;
import searchengine.repository.LemmasRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static searchengine.service.LoggingTemplates.TEMPLATE_SERVICE_API_REQUEST_STATISTICS;
import static searchengine.web.dto.MessagesTemplates.TEMPLATE_API_STATISTICS_EMPTY_DB;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SitesComponent sitesComponent;
    private final PagesComponent pagesComponent;
    private final LemmasRepository lemmasRepository;

    public StatisticsResponse getStatistics() {
        log.info(TEMPLATE_SERVICE_API_REQUEST_STATISTICS);



        List<DetailedStatisticsItem> detailedList = new ArrayList<>();

        List<Site> sites = sitesComponent.getSitesFromDBMatchWithConfigAndOtherSitesFromConfig();

        for (Site site : sites) {
            if (site.getId() == null) {
                detailedList.add(createAndFillWithDefaultParams(site));
                continue;
            }
            detailedList.add(createAndFill(site));
        }

        TotalStatistics totalStat = new TotalStatistics(
                detailedList.size(),
                detailedList
                        .stream()
                        .map(DetailedStatisticsItem::pages)
                        .reduce(0, Integer::sum),
                detailedList
                        .stream()
                        .map(DetailedStatisticsItem::lemmas)
                        .reduce(0, Integer::sum),
                detailedList
                        .stream()
                        .anyMatch(item ->
                                item.status().equals(SiteStatusType.INDEXING.toString()))
        );

        StatisticsData statDataContainer = new StatisticsData(totalStat, detailedList);

        StatisticsResponse responseContainer = new StatisticsResponse(true, statDataContainer);

//        log.info(TEMPLATE_SERVICE_RESPONSE,
//                responseContainer,
//                HttpStatusCode.valueOf(HTTP_CODE_OK));

        return responseContainer;
    }

    private DetailedStatisticsItem createAndFillWithDefaultParams(Site siteFromConfig) {
        return new DetailedStatisticsItem(
                siteFromConfig.getUrl(),
                siteFromConfig.getName(),
                SiteStatusType.FAILED.toString(),
                LocalDateTime.now()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                TEMPLATE_API_STATISTICS_EMPTY_DB,
                0,
                0);
    }

    private DetailedStatisticsItem createAndFill(Site siteFromDB) {
        int somePages = pagesComponent.countAllBySiteId(siteFromDB.getId());
        int someLemmas = lemmasRepository.countAllBySiteId(siteFromDB.getId());

        return new DetailedStatisticsItem(
                siteFromDB.getUrl(),
                siteFromDB.getName(),
                siteFromDB.getStatus().toString(),
                siteFromDB.getStatusTime()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli(),
                siteFromDB.getLastError(),
                somePages,
                someLemmas
        );
    }
}
