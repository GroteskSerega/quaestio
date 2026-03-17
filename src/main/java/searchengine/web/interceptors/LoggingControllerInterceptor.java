package searchengine.web.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import static searchengine.web.interceptors.LoggingTemplates.TEMPLATE_HTTP_REQUEST_LOGGING;
import static searchengine.web.interceptors.LoggingTemplates.TEMPLATE_HTTP_RESPONSE_LOGGING;

@Slf4j
public class LoggingControllerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        log.info(TEMPLATE_HTTP_REQUEST_LOGGING,
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getRemoteAddr());

        return HandlerInterceptor.super.preHandle(request,
                response,
                handler);
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           @Nullable ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request,
                response,
                handler,
                modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                @Nullable Exception ex) throws Exception {
        log.info(TEMPLATE_HTTP_RESPONSE_LOGGING,
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString(),
                request.getRemoteAddr(),
                response.getStatus());

        HandlerInterceptor.super.afterCompletion(request,
                response,
                handler,
                ex);
    }
}
