package it.smartcommunitylabdhub.core.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"org.springdoc"})
public class OpenApiConfig {
    @Bean
    OpenAPI coreMicroserviceOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
                .info(new Info().title("Core")
                        .description("{Piattaforma}")
                        .version("1.0"));

        return openAPI;
    }

    @Bean
    public GroupedOpenApi apiCore() {
        return GroupedOpenApi.builder()
                .group("Core Base API V1")
                .packagesToScan("it.smartcommunitylabdhub.core.controllers.v1.base")
                .build();

    }

    @Bean
    public GroupedOpenApi apiContextCore() {
        return GroupedOpenApi.builder()
                .group("Core Context API V1")
                .packagesToScan("it.smartcommunitylabdhub.core.controllers.v1.context")
                .build();

    }
}
