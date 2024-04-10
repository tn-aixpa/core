package it.smartcommunitylabdhub.core.models.indexers;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.metadata.AuditMetadata;
import it.smartcommunitylabdhub.commons.models.metadata.BaseMetadata;
import it.smartcommunitylabdhub.core.components.solr.SolrBaseEntityParser;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import java.util.Date;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.util.Assert;

public abstract class BaseEntityIndexer<T extends BaseEntity, D extends BaseDTO> implements SolrEntityIndexer<T, D> {

    protected SolrInputDocument parse(D item, String type) {
        Assert.notNull(item, "dto can not be null");

        SolrInputDocument doc = new SolrInputDocument();
        String keyGroup = SolrBaseEntityParser.buildKeyGroup(item.getKind(), item.getProject(), item.getName());
        doc.addField("keyGroup", keyGroup);
        doc.addField("type", type);
        //base doc
        doc.addField("id", item.getId());
        doc.addField("kind", item.getKind());
        doc.addField("project", item.getProject());
        doc.addField("name", item.getName());
        doc.addField("user", item.getUser());

        //status
        StatusFieldAccessor status = StatusFieldAccessor.with(item.getStatus());
        doc.addField("status", status.getState());

        //extract meta to index
        BaseMetadata metadata = BaseMetadata.from(item.getMetadata());

        //metadata
        doc.addField("metadata.project", metadata.getProject());
        doc.addField("metadata.name", metadata.getName());
        doc.addField("metadata.description", metadata.getDescription());
        doc.addField("metadata.labels", metadata.getLabels());
        doc.addField("metadata.created", Date.from(metadata.getCreated().toInstant()));
        doc.addField("metadata.updated", Date.from(metadata.getUpdated().toInstant()));

        AuditMetadata auditing = AuditMetadata.from(item.getMetadata());
        doc.addField("metadata.createdBy", auditing.getCreatedBy());
        doc.addField("metadata.updatedBy", auditing.getUpdatedBy());

        return doc;
    }
}
