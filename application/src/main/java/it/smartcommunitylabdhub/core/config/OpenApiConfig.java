package it.smartcommunitylabdhub.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ComponentScan(basePackages = {"org.springdoc"})
public class OpenApiConfig {

    @Value("${application.name}")
    private String name;

    @Value("${application.description}")
    private String description;

    @Value("${application.version}")
    private String version;

    @Bean
    OpenAPI coreMicroserviceOpenAPI() {
        return new OpenAPI().info(new Info().title(name).description(description).version(version));
    }

    @Bean
    public GroupedOpenApi apiCore() {
        return GroupedOpenApi
            .builder()
            .group("core-v1")
            .displayName("Core Base API V1")
            .packagesToScan("it.smartcommunitylabdhub.core.controllers.v1.base")
            .build();
    }

    @Bean
    public GroupedOpenApi apiContextCore() {
        return GroupedOpenApi
            .builder()
            .group("context-v1")
            .displayName("Core Context API V1")
            .packagesToScan("it.smartcommunitylabdhub.core.controllers.v1.context")
            .build();
    }
}
