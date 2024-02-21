package it.smartcommunitylabdhub.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@ConfigurationProperties(prefix = "security", ignoreUnknownFields = true)
@Getter
@Setter
public class SecurityProperties {

    @NestedConfigurationProperty
    private BasicAuthenticationProperties basic;

    @NestedConfigurationProperty
    private JwtAuthenticationProperties jwt;

    public boolean isBasicAuthEnabled() {
        return basic != null && basic.isEnabled();
    }

    public boolean isJwtAuthEnabled() {
        return jwt != null && jwt.isEnabled();
    }

    public boolean isRequired() {
        return isBasicAuthEnabled() || isJwtAuthEnabled();
    }

    @Getter
    @Setter
    public static class BasicAuthenticationProperties {

        private String username;
        private String password;

        public boolean isEnabled() {
            return StringUtils.hasText(username) && StringUtils.hasText(password);
        }
    }

    @Getter
    @Setter
    public static class JwtAuthenticationProperties {

        private String issuerUri;
        private String audience;
        private String claim;

        public boolean isEnabled() {
            return StringUtils.hasText(issuerUri) && StringUtils.hasText(audience);
        }
    }
}
