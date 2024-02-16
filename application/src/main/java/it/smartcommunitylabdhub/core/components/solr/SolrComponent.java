package it.smartcommunitylabdhub.core.components.solr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import it.smartcommunitylabdhub.commons.models.base.BaseEntity;
import it.smartcommunitylabdhub.commons.models.entities.artifact.ArtifactMetadata;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItemMetadata;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionMetadata;
import it.smartcommunitylabdhub.commons.models.entities.run.RunMetadata;
import it.smartcommunitylabdhub.commons.models.entities.secret.SecretMetadata;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskMetadata;
import it.smartcommunitylabdhub.commons.models.entities.workflow.WorkflowMetadata;
import it.smartcommunitylabdhub.core.components.cloud.events.EntityEvent;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;

@Component
@ConditionalOnProperty(name = "solr.enabled", havingValue = "true", matchIfMissing = false)
public class SolrComponent implements ApplicationListener<ContextRefreshedEvent> {
	@Value("${solr.url}")
	private String solrUrl;
	
	@Value("${solr.collection}")
	private String solrCollection;
	
	CBORConverter converter = new CBORConverter();
	
	SolrIndexManager indexManager;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			Http2SolrClient solrClient = new Http2SolrClient.Builder(solrUrl)
					.withConnectionTimeout(5000, TimeUnit.MILLISECONDS)
					.build();
			indexManager = new SolrIndexManager(solrClient, solrCollection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@PreDestroy
	public void close() {
		indexManager.close();
	}

    @EventListener
    public <T extends BaseEntity> void handleEntitySavedEvent(EntityEvent<T> event) {
		switch (event.getAction()) {
			case CREATE:
			case UPDATE:
				indexDoc(event);
				break;
			case DELETE:
			try {
				indexManager.removeDoc(getId(event));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}    	
    }
    
    private <T extends BaseEntity> void indexDoc(EntityEvent<T> event) {
    	if(event.getEntity() instanceof DataItemEntity) {
    		DataItemEntity entity = (DataItemEntity)event.getEntity();
    		indexDoc(entity);
    	} else if(event.getEntity() instanceof FunctionEntity) {
    		FunctionEntity entity = (FunctionEntity)event.getEntity();
    		indexDoc(entity);
    	} else if(event.getEntity() instanceof ArtifactEntity) {
    		ArtifactEntity entity = (ArtifactEntity)event.getEntity();
    		indexDoc(entity);
    	} else if(event.getEntity() instanceof RunEntity) {
    		RunEntity entity = (RunEntity)event.getEntity();
    		indexDoc(entity);
    	} else if(event.getEntity() instanceof TaskEntity) {
    		TaskEntity entity = (TaskEntity)event.getEntity();
    		indexDoc(entity);
    	} else if(event.getEntity() instanceof SecretEntity) {
    		SecretEntity entity = (SecretEntity)event.getEntity();
    		indexDoc(entity);
    	} else if(event.getEntity() instanceof WorkflowEntity) {
    		WorkflowEntity entity = (WorkflowEntity)event.getEntity();
    		indexDoc(entity);
    	}
    }
    
    private <T extends BaseEntity> String getId(EntityEvent<T> event) {
    	if(event.getEntity() instanceof DataItemEntity) {
    		DataItemEntity entity = (DataItemEntity)event.getEntity();
    		return entity.getId();
    	} else if(event.getEntity() instanceof FunctionEntity) {
    		FunctionEntity entity = (FunctionEntity)event.getEntity();
    		return entity.getId();
    	} else if(event.getEntity() instanceof ArtifactEntity) {
    		ArtifactEntity entity = (ArtifactEntity)event.getEntity();
    		return entity.getId();
    	} else if(event.getEntity() instanceof RunEntity) {
    		RunEntity entity = (RunEntity)event.getEntity();
    		return entity.getId();
    	} else if(event.getEntity() instanceof TaskEntity) {
    		TaskEntity entity = (TaskEntity)event.getEntity();
    		return entity.getId();
    	} else if(event.getEntity() instanceof SecretEntity) {
    		SecretEntity entity = (SecretEntity)event.getEntity();
    		return entity.getId();
    	} else if(event.getEntity() instanceof WorkflowEntity) {
    		WorkflowEntity entity = (WorkflowEntity)event.getEntity();
    		return entity.getId();
    	} 
    	return null;
    }
    
    private void indexDoc(DataItemEntity item) {
		try {
			Map<String, Serializable> metadataMap = converter.reverseConvert(item.getMetadata());
			DataItemMetadata metadata = new DataItemMetadata();
			metadata.configure(metadataMap);
			SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
			indexManager.indexDoc(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    private void indexDoc(FunctionEntity item) {
		try {
			Map<String, Serializable> metadataMap = converter.reverseConvert(item.getMetadata());
			FunctionMetadata metadata = new FunctionMetadata();
			metadata.configure(metadataMap);
			SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
			indexManager.indexDoc(doc);			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void indexDoc(ArtifactEntity item) {
		try {
			Map<String, Serializable> metadataMap = converter.reverseConvert(item.getMetadata());
			ArtifactMetadata metadata = new ArtifactMetadata();
			metadata.configure(metadataMap);
			SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
			indexManager.indexDoc(doc);			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void indexDoc(RunEntity item) {
		try {
			Map<String, Serializable> metadataMap = converter.reverseConvert(item.getMetadata());
			RunMetadata metadata = new RunMetadata();
			metadata.configure(metadataMap);
			SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
			indexManager.indexDoc(doc);			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void indexDoc(TaskEntity item) {
		try {
			Map<String, Serializable> metadataMap = converter.reverseConvert(item.getMetadata());
			TaskMetadata metadata = new TaskMetadata();
			metadata.configure(metadataMap);
			SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
			indexManager.indexDoc(doc);			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void indexDoc(SecretEntity item) {
		try {
			Map<String, Serializable> metadataMap = converter.reverseConvert(item.getMetadata());
			SecretMetadata metadata = new SecretMetadata();
			metadata.configure(metadataMap);
			SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
			indexManager.indexDoc(doc);			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void indexDoc(WorkflowEntity item) {
		try {
			Map<String, Serializable> metadataMap = converter.reverseConvert(item.getMetadata());
			WorkflowMetadata metadata = new WorkflowMetadata();
			metadata.configure(metadataMap);
			SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
			indexManager.indexDoc(doc);			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	public Page<SearchGroupResult> search(String q, String fq, Pageable pageRequest) throws Exception {
		return indexManager.search(q, fq, pageRequest);
	}
	
	public void clearIndex() {
		try {
			indexManager.clearIndex();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void indexBounce(List<DataItemEntity> docs) {
		try {
			List<SolrInputDocument> solrDocs = new ArrayList<>();
			for(DataItemEntity doc : docs) {
				Map<String, Serializable> metadataMap = converter.reverseConvert(doc.getMetadata());
				DataItemMetadata metadata = new DataItemMetadata();
				metadata.configure(metadataMap);
				solrDocs.add(SolrBaseEntityParser.parser(doc, metadata));
			}
			indexManager.indexBounce(solrDocs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
