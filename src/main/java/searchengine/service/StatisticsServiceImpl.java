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
import searchengine.repository.LemmaRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static searchengine.service.ServiceLoggingTemplates.TEMPLATE_SERVICE_API_REQUEST_STATISTICS;
import static searchengine.web.dto.MessagesTemplates.TEMPLATE_API_STATISTICS_EMPTY_DB;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SitesComponent sitesComponent;
    private final PagesComponent pagesComponent;
    private final LemmaRepository lemmaRepository;

    public StatisticsResponse getStatistics() {
        log.info(TEMPLATE_SERVICE_API_REQUEST_STATISTICS);

        List<DetailedStatisticsItem> detailedList = new ArrayList<>();

        List<Site> sites = sitesComponent.getSitesFromDBMatchWithConfigAndOtherSitesFromConfig();

        int totalPages = 0;
        int totalLemmas = 0;
        boolean isAnyIndexing = false;

        for (Site site : sites) {

            DetailedStatisticsItem item;

            if (site.getId() == null) {
                item = createAndFillWithDefaultParams(site);
//                continue;
            } else {
                item = createAndFill(site);
            }

//            detailedList.add(createAndFill(site));
            detailedList.add(item);

            totalPages += item.pages();
            totalLemmas += item.lemmas();

            if (!isAnyIndexing && SiteStatusType.INDEXING.toString().equals(item.status())) {
                isAnyIndexing = true;
            }
        }

        TotalStatistics totalStat =
                new TotalStatistics(detailedList.size(),
                        totalPages,
                        totalLemmas,
                        isAnyIndexing);

        StatisticsData statDataContainer = new StatisticsData(totalStat, detailedList);

        return new StatisticsResponse(true, statDataContainer);
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

    // TODO N+1 request. Need Calcuate via JQL
    private DetailedStatisticsItem createAndFill(Site siteFromDB) {
        int somePages = pagesComponent.countAllBySiteId(siteFromDB.getId());
        int someLemmas = lemmaRepository.countAllBySiteId(siteFromDB.getId());

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
