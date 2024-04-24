package it.smartcommunitylabdhub.core.models.indexers;

import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.metadata.VersioningMetadata;
import it.smartcommunitylabdhub.core.components.solr.IndexField;
import it.smartcommunitylabdhub.core.models.builders.dataitem.DataItemDTOBuilder;
import it.smartcommunitylabdhub.core.models.entities.DataItemEntity;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
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

        DataItem item = builder.convert(entity);
        if (item == null) {
            throw new IllegalArgumentException("invalid or null entity");
        }

        log.debug("parse dataItem {}", item.getId());
        if (log.isTraceEnabled()) {
            log.trace("item: {}", item);
        }

        //base
        SolrInputDocument doc = parse(item, TYPE);

        //add versioning
        VersioningMetadata versioning = VersioningMetadata.from(item.getMetadata());
        doc.addField("metadata.version", versioning.getVersion());

        //TODO evaluate adding spec

        if (log.isTraceEnabled()) {
            log.trace("doc: {}", doc);
        }

        return doc;
    }

    @Override
    public List<IndexField> fields() {
        List<IndexField> fields = super.fields();

        fields.add(new IndexField("metadata.version", "text_en", true, false, true, true));
        return fields;
    }

    @Override
    public void index(DataItemEntity entity) {
        Assert.notNull(entity, "entity can not be null");

        if (solr != null) {
            try {
                log.debug("index dataItem {}", entity.getId());

                SolrInputDocument doc = parse(entity);
                solr.indexDoc(doc);
            } catch (Exception e) {
                log.error("error with solr: {}", e.getMessage());
            }
        }
    }

    @Override
    public void indexAll(Collection<DataItemEntity> entities) {
        Assert.notNull(entities, "entities can not be null");
        log.debug("index {} dataItems", entities.size());

        if (solr != null) {
            try {
                List<SolrInputDocument> docs = entities.stream().map(e -> parse(e)).collect(Collectors.toList());
                solr.indexBounce(docs);
            } catch (Exception e) {
                log.error("error with solr: {}", e.getMessage());
            }
        }
    }

    @Override
    public void clearIndex() {
        log.debug("clear index for {}", TYPE);
        try {
            solr.clearIndexByType(TYPE);
        } catch (Exception e) {
            log.error("error with solr: {}", e.getMessage());
        }
    }
}
