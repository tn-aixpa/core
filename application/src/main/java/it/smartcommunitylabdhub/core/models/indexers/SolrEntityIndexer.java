package it.smartcommunitylabdhub.core.models.indexers;

import it.smartcommunitylabdhub.core.components.solr.IndexField;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import java.util.Collection;

public interface SolrEntityIndexer<T extends BaseEntity> {
    public void index(T entity);

    public void indexAll(Collection<T> entities);

    public Collection<IndexField> fields();

    public void clearIndex();
}
