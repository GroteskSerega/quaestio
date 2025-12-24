package searchengine.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static searchengine.logging.LoggingTemplates.TEMPLATE_CONFIG_APP_CONFIG;

@Slf4j
@Getter
@Setter
@ToString
@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private String name;
    private Integer threadCount;

    @PostConstruct
    public void printConfig() {
        log.info(TEMPLATE_CONFIG_APP_CONFIG, this);
    }
}
