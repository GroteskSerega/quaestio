package searchengine.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static searchengine.logging.LoggingTemplates.TEMPLATE_CONFIG_JSOUP_HEADERS_CONFIG;

@Slf4j
@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "jsoup-settings")
public class JsoupConfig {
    private String userAgent;
    private String referrer;

    @PostConstruct
    public void printJsoupSettings() {
        log.info(TEMPLATE_CONFIG_JSOUP_HEADERS_CONFIG,
                this);
    }
}
