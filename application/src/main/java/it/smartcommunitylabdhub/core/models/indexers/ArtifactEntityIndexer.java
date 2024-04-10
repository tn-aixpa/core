package it.smartcommunitylabdhub.core.models.indexers;

import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.metadata.VersioningMetadata;
import it.smartcommunitylabdhub.core.models.builders.artifact.ArtifactDTOBuilder;
import it.smartcommunitylabdhub.core.models.entities.ArtifactEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class ArtifactEntityIndexer extends BaseEntityIndexer<ArtifactEntity, Artifact> {

    private static final String TYPE = EntityName.ARTIFACT.getValue();

    private final ArtifactDTOBuilder builder;

    public ArtifactEntityIndexer(ArtifactDTOBuilder builder) {
        Assert.notNull(builder, "builder can not be null");

        this.builder = builder;
    }

    @Override
    public SolrInputDocument parse(ArtifactEntity entity) {
        Assert.notNull(entity, "entity can not be null");

        log.debug("index artifact {}", entity.getId());

        return index(builder.convert(entity));
    }

    @Override
    public SolrInputDocument index(Artifact item) {
        if (item == null) {
            throw new IllegalArgumentException("invalid or null entity");
        }

        if (log.isTraceEnabled()) {
            log.trace("item: {}", item);
        }

        //base
        SolrInputDocument doc = parse(item, TYPE);

        //add versioning
        VersioningMetadata versioning = VersioningMetadata.from(item.getMetadata());
        doc.addField("version", versioning.getVersion());

        //TODO evaluate adding spec

        if (log.isTraceEnabled()) {
            log.trace("doc: {}", doc);
        }

        return doc;
    }
}
