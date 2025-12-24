package searchengine.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static searchengine.logging.LoggingTemplates.TEMPLATE_CONFIG_SITES_CONFIG;

@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "indexing-settings")
public class SitesListConfig {
    private List<SiteConfig> sites;

    @PostConstruct
    public void printIndexingSettings() {
        log.info(TEMPLATE_CONFIG_SITES_CONFIG, this);
    }

    @Override
    public String toString() {
        return "SitesList{" +
                "sites=" + Arrays.toString(sites.toArray()) +
                '}';
    }
}
