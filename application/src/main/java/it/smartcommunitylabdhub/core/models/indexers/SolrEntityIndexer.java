package it.smartcommunitylabdhub.core.models.indexers;

import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import org.apache.solr.common.SolrInputDocument;

public interface SolrEntityIndexer<T extends BaseEntity, D extends BaseDTO> {
    public SolrInputDocument parse(T entity);

    public SolrInputDocument index(D dto);
}
