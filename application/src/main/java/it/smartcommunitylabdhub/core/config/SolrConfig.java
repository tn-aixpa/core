package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.core.components.solr.SolrProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Order(3)
@EnableConfigurationProperties({ SolrProperties.class })
public class SolrConfig {}
