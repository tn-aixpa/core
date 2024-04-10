package it.smartcommunitylabdhub.core.components.solr;

import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.core.models.indexers.ArtifactEntityIndexer;
import it.smartcommunitylabdhub.core.models.indexers.DataItemEntityIndexer;
import it.smartcommunitylabdhub.core.models.indexers.FunctionEntityIndexer;
import it.smartcommunitylabdhub.core.models.indexers.WorkflowEntityIndexer;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Value("${solr.url}")
    private String solrUrl;

    @Value("${solr.collection}")
    private String solrCollection;

    //indexers
    //TODO remove
    @Autowired
    private ArtifactEntityIndexer artifactIndexer;

    @Autowired
    private DataItemEntityIndexer dataItemIndexer;

    @Autowired
    private FunctionEntityIndexer functionIndexer;

    @Autowired
    private WorkflowEntityIndexer workEntityIndexer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            if (indexManager == null) {
                indexManager = new SolrIndexManager(solrUrl, solrCollection);
            }

            //init
            indexManager.init();
        } catch (Exception e) {
            SolrComponent.log.error("onApplicationEvent", e);
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

    public SolrPage<SearchGroupResult> groupSearch(String q, List<String> fq, Pageable pageRequest) throws Exception {
        return indexManager.groupSearch(q, fq, pageRequest);
    }

    public SolrPage<ItemResult> itemSearch(String q, List<String> fq, Pageable pageRequest) throws Exception {
        return indexManager.itemSearch(q, fq, pageRequest);
    }

    public void clearIndex() {
        try {
            indexManager.clearIndex();
        } catch (Exception e) {
            SolrComponent.log.error("clearIndex", e);
        }
    }

    //TODO move
    public void indexBounceDataItem(List<DataItem> docs) {
        try {
            List<SolrInputDocument> solrDocs = docs
                .stream()
                .map(d -> dataItemIndexer.index(d))
                .collect(Collectors.toList());
            indexBounce(solrDocs);
        } catch (Exception e) {
            SolrComponent.log.error("indexBounceDataItem:DataItem", e);
        }
    }

    public void indexBounceFunction(List<Function> docs) {
        try {
            List<SolrInputDocument> solrDocs = docs
                .stream()
                .map(d -> functionIndexer.index(d))
                .collect(Collectors.toList());
            indexBounce(solrDocs);
        } catch (Exception e) {
            SolrComponent.log.error("indexBounceDataItem:Function", e);
        }
    }

    public void indexBounceArtifact(List<Artifact> docs) {
        try {
            List<SolrInputDocument> solrDocs = docs
                .stream()
                .map(d -> artifactIndexer.index(d))
                .collect(Collectors.toList());
            indexBounce(solrDocs);
        } catch (Exception e) {
            SolrComponent.log.error("indexBounceDataItem:Artifact", e);
        }
    }
}
