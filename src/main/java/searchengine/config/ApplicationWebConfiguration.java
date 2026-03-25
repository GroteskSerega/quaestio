package searchengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import searchengine.web.interceptors.LoggingControllerInterceptor;

public class ApplicationWebConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingControllerInterceptor());
    }

    @Bean
    public LoggingControllerInterceptor loggingControllerInterceptor() {
        return new LoggingControllerInterceptor();
    }
}
