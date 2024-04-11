package it.smartcommunitylabdhub.core.components.solr;

import java.util.List;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Slf4j
@ConditionalOnProperty(name = "solr.enabled", havingValue = "true", matchIfMissing = false)
public class SolrComponent implements ApplicationListener<ContextRefreshedEvent> {

    private SolrIndexManager indexManager;

    @Value("${solr.reindex}")
    private String solrReindex;

    private boolean reindexed = false;

    public SolrComponent(@Value("${solr.url}") String solrUrl, @Value("${solr.collection}") String solrCollection) {
        Assert.hasText(solrUrl, "solr url is required");
        Assert.hasText(solrCollection, "solr collection is required");

        //build manager
        this.indexManager = new SolrIndexManager(solrUrl, solrCollection);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            //init
            indexManager.init();

            if ("always".equals(solrReindex) && !reindexed) {
                //TODO
            }
        } catch (Exception e) {
            log.error("onApplicationEvent", e);
        }
    }

    @PreDestroy
    public void close() {
        indexManager.close();
    }

    public void indexDoc(SolrInputDocument doc) throws Exception {
        Assert.notNull(doc, "doc can not be null");
        if (doc.getField("id") == null) {
            throw new IllegalArgumentException("missing or invalid id in doc");
        }

        if (doc.getField("type") == null) {
            throw new IllegalArgumentException("missing or invalid type in doc");
        }

        indexManager.indexDoc(doc);
    }

    public void removeDoc(String id) throws Exception {
        Assert.notNull(id, "id can not be null");
        indexManager.removeDoc(id);
    }

    public void indexBounce(Iterable<SolrInputDocument> docs) throws Exception {
        Assert.notNull(docs, "docs can not be null");

        indexManager.indexBounce(docs);
    }

    public void registerFields(Iterable<IndexField> fields) throws Exception {
        Assert.notNull(fields, "fields can not be null");
        indexManager.initFields(fields);
    }

    public SolrPage<SearchGroupResult> groupSearch(String q, List<String> fq, Pageable pageRequest) throws Exception {
        return indexManager.groupSearch(q, fq, pageRequest);
    }

    public SolrPage<ItemResult> itemSearch(String q, List<String> fq, Pageable pageRequest) throws Exception {
        return indexManager.itemSearch(q, fq, pageRequest);
    }

    public void clearIndex() throws Exception {
    	indexManager.clearIndex();
    }
    
    public void clearIndexByType(String type) throws Exception {
    	indexManager.clearIndexByType(type);
    }
    
}
