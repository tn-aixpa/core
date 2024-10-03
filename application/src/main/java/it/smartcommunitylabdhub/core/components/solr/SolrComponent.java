package it.smartcommunitylabdhub.core.components.solr;

import java.util.List;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "solr", name = "url")
public class SolrComponent implements InitializingBean {

    private SolrIndexManager indexManager;

    public SolrComponent(SolrProperties solrProperties) {
        Assert.notNull(solrProperties, "solr properties are required");

        //build manager
        this.indexManager = new SolrIndexManager(solrProperties);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(indexManager, "index manager missing");
        try {
            //init
            indexManager.init();
        } catch (SolrIndexerException e) {
            log.error("error initializing solr: {}", e.getMessage());
            indexManager = null;
        }
    }

    @PreDestroy
    public void close() {
        try {
            if (indexManager != null) {
                indexManager.close();
            }
        } catch (SolrIndexerException e) {
            log.error("error disconnecting solr: {}", e.getMessage());
        }
    }

    public void indexDoc(SolrInputDocument doc) throws SolrIndexerException {
        Assert.notNull(doc, "doc can not be null");
        if (doc.getField("id") == null) {
            throw new IllegalArgumentException("missing or invalid id in doc");
        }

        if (doc.getField("type") == null) {
            throw new IllegalArgumentException("missing or invalid type in doc");
        }
        if (indexManager != null) {
            indexManager.indexDoc(doc);
        }
    }

    public void removeDoc(String id) throws SolrIndexerException {
        Assert.notNull(id, "id can not be null");
        if (indexManager != null) {
            indexManager.removeDoc(id);
        }
    }

    public void indexBounce(Iterable<SolrInputDocument> docs) throws SolrIndexerException {
        Assert.notNull(docs, "docs can not be null");
        if (indexManager != null) {
            indexManager.indexBounce(docs);
        }
    }

    public void registerFields(Iterable<IndexField> fields) throws SolrIndexerException {
        Assert.notNull(fields, "fields can not be null");
        if (indexManager != null) {
            indexManager.initFields(fields);
        }
    }

    public SolrPage<SearchGroupResult> groupSearch(String q, List<String> fq, Pageable pageRequest)
        throws SolrIndexerException {
        if (indexManager == null) {
            throw new SolrIndexerException("solr not available");
        }
        return indexManager.groupSearch(q, fq, pageRequest);
    }

    public SolrPage<ItemResult> itemSearch(String q, List<String> fq, Pageable pageRequest)
        throws SolrIndexerException {
        if (indexManager == null) {
            throw new SolrIndexerException("solr not available");
        }
        return indexManager.itemSearch(q, fq, pageRequest);
    }

    public void clearIndex() throws SolrIndexerException {
        if (indexManager != null) {
            indexManager.clearIndex();
        }
    }

    public void clearIndexByType(String type) throws SolrIndexerException {
        if (indexManager != null) {
            indexManager.clearIndexByType(type);
        }
    }
}
