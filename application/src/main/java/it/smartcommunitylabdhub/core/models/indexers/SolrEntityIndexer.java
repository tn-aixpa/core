package it.smartcommunitylabdhub.core.models.indexers;

import it.smartcommunitylabdhub.core.components.solr.IndexField;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import java.util.Collection;
import org.apache.solr.common.SolrInputDocument;

public interface SolrEntityIndexer<T extends BaseEntity> {
    public SolrInputDocument parse(T entity);

    public void index(T entity);

    public void indexAll(Collection<T> entities);

    public Collection<IndexField> fields();
}
