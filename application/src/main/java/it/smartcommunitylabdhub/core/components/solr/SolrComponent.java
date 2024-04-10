package it.smartcommunitylabdhub.core.components.solr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.artifact.ArtifactMetadata;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItemMetadata;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionMetadata;
import it.smartcommunitylabdhub.core.components.cloud.CloudEntityEvent;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConditionalOnProperty(name = "solr.enabled", havingValue = "true", matchIfMissing = false)
public class SolrComponent implements ApplicationListener<ContextRefreshedEvent> {

    SolrIndexManager indexManager;

    @Value("${solr.url}")
    private String solrUrl;

    @Value("${solr.collection}")
    private String solrCollection;
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
        	if(indexManager == null)
        		indexManager = new SolrIndexManager(solrUrl, solrCollection);
        } catch (Exception e) {
            SolrComponent.log.error("onApplicationEvent", e);
        }
    }

    @PreDestroy
    public void close() {
        indexManager.close();
    }
    
    @Async
    @EventListener
    public void receive(CloudEntityEvent<? extends BaseDTO> event) {
        switch (event.getAction()) {
	        case CREATE:
	        case UPDATE:
	            indexDoc(event);
	            break;
	        case DELETE:
	            try {
	                indexManager.removeDoc(event.getDto().getId());
	            } catch (Exception e) {
	                SolrComponent.log.error("handleEntitySavedEvent:DELETE", e);
	            }
	            break;
	        case READ:
	        	break;
        }
    }
    
    private <T extends BaseDTO> void indexDoc(CloudEntityEvent<T> event) {
        if (event.getDto() instanceof DataItem) {
            DataItem entity = (DataItem) event.getDto();
            indexDoc(entity);
        } else if (event.getDto() instanceof Function) {
            Function entity = (Function) event.getDto();
            indexDoc(entity);
        } else if (event.getDto() instanceof Artifact) {
            Artifact entity = (Artifact) event.getDto();
            indexDoc(entity);
        }
    }

    private void indexDoc(DataItem item) {
        try {
            Map<String, Serializable> metadataMap = item.getMetadata();
            DataItemMetadata metadata = new DataItemMetadata();
            metadata.configure(metadataMap);
            SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
            indexManager.indexDoc(doc);
        } catch (Exception e) {
            SolrComponent.log.error("indexDoc:DataItem", e);
        }
    }

    private void indexDoc(Function item) {
        try {
            Map<String, Serializable> metadataMap = item.getMetadata();
            FunctionMetadata metadata = new FunctionMetadata();
            metadata.configure(metadataMap);
            SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
            indexManager.indexDoc(doc);
        } catch (Exception e) {
            SolrComponent.log.error("indexDoc:Function", e);
        }
    }

    private void indexDoc(Artifact item) {
        try {
            Map<String, Serializable> metadataMap = item.getMetadata();
            ArtifactMetadata metadata = new ArtifactMetadata();
            metadata.configure(metadataMap);
            SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
            indexManager.indexDoc(doc);
        } catch (Exception e) {
            SolrComponent.log.error("indexDoc:Artifact", e);
        }
    }

    //    private void indexDoc(Run item) {
    //        try {
    //            Map<String, Serializable> metadataMap = item.getMetadata();
    //            RunMetadata metadata = new RunMetadata();
    //            metadata.configure(metadataMap);
    //            SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
    //            indexManager.indexDoc(doc);
    //        } catch (Exception e) {
    //        	SolrComponent.log.error("indexDoc:Run", e);
    //        }
    //    }

    //    private void indexDoc(Task item) {
    //		try {
    //			Map<String, Serializable> metadataMap = item.getMetadata();
    //			TaskMetadata metadata = new TaskMetadata();
    //			metadata.configure(metadataMap);
    //			SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
    //			indexManager.indexDoc(doc);
    //		} catch (Exception e) {
    //			e.printStackTrace();
    //		}
    //    }

    //    private void indexDoc(Secret item) {
    //        try {
    //            Map<String, Serializable> metadataMap = item.getMetadata();
    //            SecretMetadata metadata = new SecretMetadata();
    //            metadata.configure(metadataMap);
    //            SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
    //            indexManager.indexDoc(doc);
    //        } catch (Exception e) {
    //        	SolrComponent.log.error("indexDoc:Secret", e);
    //        }
    //    }

    //    private void indexDoc(Workflow item) {
    //        try {
    //            Map<String, Serializable> metadataMap = item.getMetadata();
    //            WorkflowMetadata metadata = new WorkflowMetadata();
    //            metadata.configure(metadataMap);
    //            SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
    //            indexManager.indexDoc(doc);
    //        } catch (Exception e) {
    //        	SolrComponent.log.error("indexDoc:Workflow", e);
    //        }
    //    }

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

    public void indexBounceDataItem(List<DataItem> docs) {
        try {
            List<SolrInputDocument> solrDocs = new ArrayList<>();
            for (DataItem doc : docs) {
                Map<String, Serializable> metadataMap = doc.getMetadata();
                DataItemMetadata metadata = new DataItemMetadata();
                metadata.configure(metadataMap);
                solrDocs.add(SolrBaseEntityParser.parser(doc, metadata));
            }
            indexManager.indexBounce(solrDocs);
        } catch (Exception e) {
            SolrComponent.log.error("indexBounceDataItem:DataItem", e);
        }
    }

    public void indexBounceFunction(List<Function> docs) {
        try {
            List<SolrInputDocument> solrDocs = new ArrayList<>();
            for (Function doc : docs) {
                Map<String, Serializable> metadataMap = doc.getMetadata();
                FunctionMetadata metadata = new FunctionMetadata();
                metadata.configure(metadataMap);
                solrDocs.add(SolrBaseEntityParser.parser(doc, metadata));
            }
            indexManager.indexBounce(solrDocs);
        } catch (Exception e) {
            SolrComponent.log.error("indexBounceDataItem:Function", e);
        }
    }

    public void indexBounceArtifact(List<Artifact> docs) {
        try {
            List<SolrInputDocument> solrDocs = new ArrayList<>();
            for (Artifact doc : docs) {
                Map<String, Serializable> metadataMap = doc.getMetadata();
                ArtifactMetadata metadata = new ArtifactMetadata();
                metadata.configure(metadataMap);
                solrDocs.add(SolrBaseEntityParser.parser(doc, metadata));
            }
            indexManager.indexBounce(solrDocs);
        } catch (Exception e) {
            SolrComponent.log.error("indexBounceDataItem:Artifact", e);
        }
    }
    //    public void indexBounceRun(List<Run> docs) {
    //        try {
    //            List<SolrInputDocument> solrDocs = new ArrayList<>();
    //            for (Run doc : docs) {
    //                Map<String, Serializable> metadataMap = doc.getMetadata();
    //                RunMetadata metadata = new RunMetadata();
    //                metadata.configure(metadataMap);
    //                solrDocs.add(SolrBaseEntityParser.parser(doc, metadata));
    //            }
    //            indexManager.indexBounce(solrDocs);
    //        } catch (Exception e) {
    //        	SolrComponent.log.error("indexBounceDataItem:Run", e);
    //        }
    //    }

    //    public void indexBounceSecret(List<Secret> docs) {
    //        try {
    //            List<SolrInputDocument> solrDocs = new ArrayList<>();
    //            for (Secret doc : docs) {
    //                Map<String, Serializable> metadataMap = doc.getMetadata();
    //                SecretMetadata metadata = new SecretMetadata();
    //                metadata.configure(metadataMap);
    //                solrDocs.add(SolrBaseEntityParser.parser(doc, metadata));
    //            }
    //            indexManager.indexBounce(solrDocs);
    //        } catch (Exception e) {
    //        	SolrComponent.log.error("indexBounceDataItem:Secret", e);
    //        }
    //    }

    //    public void indexBounceWorkflow(List<Workflow> docs) {
    //        try {
    //            List<SolrInputDocument> solrDocs = new ArrayList<>();
    //            for (Workflow doc : docs) {
    //                Map<String, Serializable> metadataMap = doc.getMetadata();
    //                WorkflowMetadata metadata = new WorkflowMetadata();
    //                metadata.configure(metadataMap);
    //                solrDocs.add(SolrBaseEntityParser.parser(doc, metadata));
    //            }
    //            indexManager.indexBounce(solrDocs);
    //        } catch (Exception e) {
    //        	SolrComponent.log.error("indexBounceDataItem:Workflow", e);
    //        }
    //    }
}
