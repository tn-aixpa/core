package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.commons.config.ApplicationProperties;
import it.smartcommunitylabdhub.commons.config.SecurityProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Order(1)
@EnableConfigurationProperties({ ApplicationProperties.class, SecurityProperties.class })
public class AppConfig {}
