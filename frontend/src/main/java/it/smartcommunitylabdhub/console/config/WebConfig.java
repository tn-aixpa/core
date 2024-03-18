package it.smartcommunitylabdhub.console.config;

import it.smartcommunitylabdhub.console.Keys;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // user console dist
        registry
            .addResourceHandler(Keys.CONSOLE_CONTEXT + "/**")
            .addResourceLocations("classpath:/console/")
            .setCachePeriod(60 * 60 * 24 * 365)/* one year */
            .resourceChain(true)
            .addResolver(new EncodedResourceResolver())
            .addResolver(new PathResourceResolver());
    }
}
