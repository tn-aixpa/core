package it.smartcommunitylabdhub.core.config;

import it.smartcommunitylabdhub.core.components.lucene.LuceneProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Order(3)
@EnableConfigurationProperties({ LuceneProperties.class })
public class LuceneConfig {}
