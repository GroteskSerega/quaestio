package searchengine.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ForkJoinPool;

@Configuration
public class IndexingPoolConfig {

    @Value("${app.searchEngineForkJoinPoolLimit}")
    private int searchEngineForkJoinPoolLimit;

    @Bean
    public ForkJoinPool forkJoinPoolConfig() {
        return new ForkJoinPool(searchEngineForkJoinPoolLimit);
    }
}
