package it.smartcommunitylabdhub.core.models.indexers;

import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.metadata.VersioningMetadata;
import it.smartcommunitylabdhub.core.models.builders.dataitem.DataItemDTOBuilder;
import it.smartcommunitylabdhub.core.models.entities.DataItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
public class DataItemEntityIndexer extends BaseEntityIndexer<DataItemEntity, DataItem> {

    private static final String TYPE = EntityName.DATAITEM.getValue();

    private final DataItemDTOBuilder builder;

    public DataItemEntityIndexer(DataItemDTOBuilder builder) {
        Assert.notNull(builder, "builder can not be null");

        this.builder = builder;
    }

    @Override
    public SolrInputDocument parse(DataItemEntity entity) {
        Assert.notNull(entity, "entity can not be null");

        log.debug("index dataItem {}", entity.getId());

        return index(builder.convert(entity));
    }

    @Override
    public SolrInputDocument index(DataItem item) {
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
