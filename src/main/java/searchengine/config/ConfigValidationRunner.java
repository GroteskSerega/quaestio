package searchengine.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import static searchengine.logging.LoggingTemplates.TEMPLATE_CONFIG_SITES_IS_EMPTY;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConfigValidationRunner implements CommandLineRunner {

    private final SitesListConfig sitesListConfig;
    private final ConfigurableApplicationContext context;

    @Override
    public void run(String... args) throws Exception {
        if (sitesListConfig.getSites().isEmpty()) {
            log.error(TEMPLATE_CONFIG_SITES_IS_EMPTY);
            SpringApplication.exit(context, () -> 1);
        }
    }
}
