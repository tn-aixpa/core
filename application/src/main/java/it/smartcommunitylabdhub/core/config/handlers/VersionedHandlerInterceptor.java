package it.smartcommunitylabdhub.core.config.handlers;

import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class VersionedHandlerInterceptor implements HandlerInterceptor {

    private final RequestMatcher requestMatcher;

    private ApplicationProperties applicationProperties;

    public VersionedHandlerInterceptor(String filterProcessingUrl) {
        Assert.hasText(filterProcessingUrl, "filter url can not be null");

        this.requestMatcher = new AntPathRequestMatcher(filterProcessingUrl);
    }

    @Autowired
    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void postHandle(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        ModelAndView modelAndView
    ) throws Exception {
        if (requestMatcher.matches(request) && applicationProperties != null) {
            //add headers
            response.addHeader("X-Api-Version", applicationProperties.getVersion());
            response.addHeader("X-Api-Level", applicationProperties.getLevel());
        }
    }
}
