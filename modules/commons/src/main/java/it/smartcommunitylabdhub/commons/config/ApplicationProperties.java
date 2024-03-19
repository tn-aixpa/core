package it.smartcommunitylabdhub.commons.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true)
@Getter
@Setter
public class ApplicationProperties {

    private String endpoint;
    private String name;
    private String description;
    private String version;
    private List<String> profiles;
}
