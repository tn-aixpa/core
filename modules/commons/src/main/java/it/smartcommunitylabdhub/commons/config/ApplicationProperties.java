package it.smartcommunitylabdhub.commons.config;

import it.smartcommunitylabdhub.commons.Keys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

// @Configuration
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true)
@Getter
public class ApplicationProperties {

    @NotBlank
    private String endpoint;

    @NotBlank
    @Pattern(regexp = Keys.SLUG_PATTERN)
    private String name;

    private String description;
    private String version;
    private String level;
    private String api;
    private List<String> profiles;

    // @ConstructorBinding
    public ApplicationProperties(
        String endpoint,
        String name,
        String description,
        String version,
        String level,
        String api,
        List<String> profiles
    ) {
        this.endpoint = endpoint;
        this.name = name;
        this.description = description;
        this.version = version;
        this.profiles = profiles;

        //level can be automagically extracted from version
        this.level = StringUtils.hasText(level) ? level : inferApiLevel(version);

        this.api = api;
    }

    public static String inferApiLevel(String version) {
        if (version == null) {
            return null;
        }

        String[] values = version.replaceAll("[^\\d.]", "").split("\\.");
        if (values.length < 2) {
            return null;
        }

        try {
            int major = Integer.parseInt(values[0]);
            int minor = Integer.parseInt(values[1]);

            int level = (major * 13) + minor;

            return Integer.toString(level);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
