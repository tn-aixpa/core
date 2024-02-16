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
import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.artifact.ArtifactMetadata;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItemMetadata;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionMetadata;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunMetadata;
import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.commons.models.entities.secret.SecretMetadata;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskMetadata;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.commons.models.entities.workflow.WorkflowMetadata;
import it.smartcommunitylabdhub.core.components.cloud.events.EntityEvent;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
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
    	if(event.getEntity() instanceof DataItem) {
    		DataItem entity = (DataItem)event.getEntity();
    		indexDoc(entity);
    	} else if(event.getEntity() instanceof FunctionEntity) {
    		Function entity = (Function)event.getEntity();
    		indexDoc(entity);
    	} else if(event.getEntity() instanceof Artifact) {
    		Artifact entity = (Artifact)event.getEntity();
    		indexDoc(entity);
    	} else if(event.getEntity() instanceof Run) {
    		Run entity = (Run)event.getEntity();
    		indexDoc(entity);
    	} else if(event.getEntity() instanceof Task) {
    		Task entity = (Task)event.getEntity();
    		indexDoc(entity);
    	} else if(event.getEntity() instanceof Secret) {
    		Secret entity = (Secret)event.getEntity();
    		indexDoc(entity);
    	} else if(event.getEntity() instanceof Workflow) {
    		Workflow entity = (Workflow)event.getEntity();
    		indexDoc(entity);
    	}
    }
    
    private <T extends BaseEntity> String getId(EntityEvent<T> event) {
    	if(event.getEntity() instanceof DataItem) {
    		DataItem entity = (DataItem)event.getEntity();
    		return entity.getId();
    	} else if(event.getEntity() instanceof Function) {
    		Function entity = (Function)event.getEntity();
    		return entity.getId();
    	} else if(event.getEntity() instanceof Artifact) {
    		Artifact entity = (Artifact)event.getEntity();
    		return entity.getId();
    	} else if(event.getEntity() instanceof Run) {
    		Run entity = (Run)event.getEntity();
    		return entity.getId();
    	} else if(event.getEntity() instanceof Task) {
    		Task entity = (Task)event.getEntity();
    		return entity.getId();
    	} else if(event.getEntity() instanceof Secret) {
    		Secret entity = (Secret)event.getEntity();
    		return entity.getId();
    	} else if(event.getEntity() instanceof Workflow) {
    		Workflow entity = (Workflow)event.getEntity();
    		return entity.getId();
    	} 
    	return null;
    }
    
    private void indexDoc(DataItem item) {
		try {
			Map<String, Serializable> metadataMap = item.getMetadata();
			DataItemMetadata metadata = new DataItemMetadata();
			metadata.configure(metadataMap);
			SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
			indexManager.indexDoc(doc);
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
    }
    
    private void indexDoc(Run item) {
		try {
			Map<String, Serializable> metadataMap = item.getMetadata();
			RunMetadata metadata = new RunMetadata();
			metadata.configure(metadataMap);
			SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
			indexManager.indexDoc(doc);			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void indexDoc(Task item) {
		try {
			Map<String, Serializable> metadataMap = item.getMetadata();
			TaskMetadata metadata = new TaskMetadata();
			metadata.configure(metadataMap);
			SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
			indexManager.indexDoc(doc);			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void indexDoc(Secret item) {
		try {
			Map<String, Serializable> metadataMap = item.getMetadata();
			SecretMetadata metadata = new SecretMetadata();
			metadata.configure(metadataMap);
			SolrInputDocument doc = SolrBaseEntityParser.parser(item, metadata);
			indexManager.indexDoc(doc);			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void indexDoc(Workflow item) {
		try {
			Map<String, Serializable> metadataMap = item.getMetadata();
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
	
	public void indexBounce(List<DataItem> docs) {
		try {
			List<SolrInputDocument> solrDocs = new ArrayList<>();
			for(DataItem doc : docs) {
				Map<String, Serializable> metadataMap = doc.getMetadata();
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
