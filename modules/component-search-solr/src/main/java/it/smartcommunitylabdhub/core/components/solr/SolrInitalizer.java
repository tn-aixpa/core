package it.smartcommunitylabdhub.core.components.solr;

import it.smartcommunitylabdhub.core.models.indexers.IndexableEntityService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "solr", name = "url")
public class SolrInitalizer implements ApplicationListener<ApplicationStartedEvent> {

    private final SolrProperties properties;
    private List<IndexableEntityService<?>> services;

    public SolrInitalizer(SolrProperties solrProperties) {
        Assert.notNull(solrProperties, "solr properties are required");
        this.properties = solrProperties;
    }

    @Autowired(required = false)
    public void setServices(List<IndexableEntityService<?>> services) {
        this.services = services;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        // trigger reindex if required
        if (services != null && "always".equals(properties.getReindex())) {
            //reindex
            services.forEach(service -> service.reindexAll());
        }
    }
}
