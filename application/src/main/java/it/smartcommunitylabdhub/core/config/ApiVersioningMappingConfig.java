package it.smartcommunitylabdhub.core.config;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import it.smartcommunitylabdhub.core.config.handlers.VersionedHandlerMapping;

@Configuration
public class ApiVersioningMappingConfig implements WebMvcRegistrations {

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new VersionedHandlerMapping();
    }

}
