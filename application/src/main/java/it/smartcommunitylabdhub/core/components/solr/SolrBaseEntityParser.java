package it.smartcommunitylabdhub.core.components.solr;

import org.apache.solr.common.SolrInputDocument;

import it.smartcommunitylabdhub.commons.models.entities.artifact.ArtifactMetadata;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItemMetadata;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionMetadata;
import it.smartcommunitylabdhub.commons.models.entities.run.RunMetadata;
import it.smartcommunitylabdhub.commons.models.entities.secret.SecretMetadata;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskMetadata;
import it.smartcommunitylabdhub.commons.models.entities.workflow.WorkflowMetadata;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;

public class SolrBaseEntityParser {
	
	public static String getKeyGroup(String kind, String project, String name) {
		return kind + "_" + project + "_" + name;
	}
	
	public static SolrInputDocument parser(DataItemEntity item, DataItemMetadata metadata) {
    	String keyGroup = getKeyGroup(item.getKind(), item.getProject(), item.getName());
		
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", item.getId());
		doc.addField("name", item.getName());
		doc.addField("project", item.getProject());
		doc.addField("kind", item.getKind());
		doc.addField("type", "dataitem");
		doc.addField("keyGroup", keyGroup);
		
		doc.addField("metadata.name", metadata.getName());
		doc.addField("metadata.description", metadata.getDescription());
		doc.addField("metadata.project", metadata.getProject());
		doc.addField("metadata.version", metadata.getVersion());
		doc.addField("metadata.created", metadata.getCreated());
		doc.addField("metadata.updated", metadata.getUpdated());
		doc.addField("metadata.labels", metadata.getLabels());
		
		return doc;
	}
	
	public static SolrInputDocument parser(FunctionEntity item, FunctionMetadata metadata) {
    	String keyGroup = getKeyGroup(item.getKind(), item.getProject(), item.getName());
    	
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", item.getId());
		doc.addField("name", item.getName());
		doc.addField("project", item.getProject());
		doc.addField("kind", item.getKind());
		doc.addField("type", "function");
		doc.addField("keyGroup", keyGroup);
		
		doc.addField("metadata.name", metadata.getName());
		doc.addField("metadata.description", metadata.getDescription());
		doc.addField("metadata.project", metadata.getProject());
		doc.addField("metadata.version", metadata.getVersion());
		doc.addField("metadata.created", metadata.getCreated());
		doc.addField("metadata.updated", metadata.getUpdated());
		doc.addField("metadata.labels", metadata.getLabels());
    	
		return doc;
	}
	
	public static SolrInputDocument parser(ArtifactEntity item, ArtifactMetadata metadata) {
    	String keyGroup = getKeyGroup(item.getKind(), item.getProject(), item.getName());
    	
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", item.getId());
		doc.addField("name", item.getName());
		doc.addField("project", item.getProject());
		doc.addField("kind", item.getKind());
		doc.addField("type", "artifact");
		doc.addField("keyGroup", keyGroup);
		
		doc.addField("metadata.name", metadata.getName());
		doc.addField("metadata.description", metadata.getDescription());
		doc.addField("metadata.project", metadata.getProject());
		doc.addField("metadata.version", metadata.getVersion());
		doc.addField("metadata.created", metadata.getCreated());
		doc.addField("metadata.updated", metadata.getUpdated());
		doc.addField("metadata.labels", metadata.getLabels());
    	
		return doc;
	}
	
	public static SolrInputDocument parser(RunEntity item, RunMetadata metadata) {
    	String keyGroup = getKeyGroup(item.getKind(), item.getProject(), item.getTask());
    	
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", item.getId());
		doc.addField("name", item.getTask()); //TODO name in run?
		doc.addField("project", item.getProject());
		doc.addField("kind", item.getKind());
		doc.addField("type", "run");
		doc.addField("keyGroup", keyGroup);
		
		doc.addField("metadata.name", metadata.getName());
		doc.addField("metadata.description", metadata.getDescription());
		doc.addField("metadata.version", metadata.getVersion());
		doc.addField("metadata.created", metadata.getCreated());
		doc.addField("metadata.updated", metadata.getUpdated());
		doc.addField("metadata.labels", metadata.getLabels());
    	
		return doc;
	}
	
	public static SolrInputDocument parser(TaskEntity item, TaskMetadata metadata) {
    	String keyGroup = getKeyGroup(item.getKind(), item.getProject(), "???");
    	
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", item.getId());
		doc.addField("name", "???"); //TODO name in task?
		doc.addField("project", item.getProject());
		doc.addField("kind", item.getKind());
		doc.addField("type", "task");
		doc.addField("keyGroup", keyGroup);
		
		doc.addField("metadata.name", metadata.getName());
		doc.addField("metadata.description", metadata.getDescription());
		doc.addField("metadata.version", metadata.getVersion());
		doc.addField("metadata.created", metadata.getCreated());
		doc.addField("metadata.updated", metadata.getUpdated());
		doc.addField("metadata.labels", metadata.getLabels());
    	
		return doc;
	}

	public static SolrInputDocument parser(SecretEntity item, SecretMetadata metadata) {
    	String keyGroup = getKeyGroup(item.getKind(), item.getProject(), item.getName());
    	
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", item.getId());
		doc.addField("name", item.getName());
		doc.addField("project", item.getProject());
		doc.addField("kind", item.getKind());
		doc.addField("type", "secret");
		doc.addField("keyGroup", keyGroup);
		
		doc.addField("metadata.name", metadata.getName());
		doc.addField("metadata.description", metadata.getDescription());
		doc.addField("metadata.project", metadata.getProject());
		doc.addField("metadata.version", metadata.getVersion());
		doc.addField("metadata.created", metadata.getCreated());
		doc.addField("metadata.updated", metadata.getUpdated());
		doc.addField("metadata.labels", metadata.getLabels());
    	
		return doc;
	}

	public static SolrInputDocument parser(WorkflowEntity item, WorkflowMetadata metadata) {
    	String keyGroup = getKeyGroup(item.getKind(), item.getProject(), item.getName());
    	
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", item.getId());
		doc.addField("name", item.getName());
		doc.addField("project", item.getProject());
		doc.addField("kind", item.getKind());
		doc.addField("type", "workflow");
		doc.addField("keyGroup", keyGroup);
		
		doc.addField("metadata.name", metadata.getName());
		doc.addField("metadata.description", metadata.getDescription());
		doc.addField("metadata.project", metadata.getProject());
		doc.addField("metadata.version", metadata.getVersion());
		doc.addField("metadata.created", metadata.getCreated());
		doc.addField("metadata.updated", metadata.getUpdated());
		doc.addField("metadata.labels", metadata.getLabels());
    	
		return doc;
	}
	
	
}
