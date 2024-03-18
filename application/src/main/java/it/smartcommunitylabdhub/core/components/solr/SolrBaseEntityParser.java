package it.smartcommunitylabdhub.core.components.solr;

import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.artifact.ArtifactMetadata;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItemMetadata;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionMetadata;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import org.apache.solr.common.SolrInputDocument;

public class SolrBaseEntityParser {

    public static String getKeyGroup(String kind, String project, String name) {
        return kind + "_" + project + "_" + name;
    }

    public static SolrInputDocument parser(DataItem item, DataItemMetadata metadata) {
        String keyGroup = getKeyGroup(item.getKind(), item.getProject(), item.getName());

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", item.getId());
        doc.addField("name", item.getName());
        doc.addField("project", item.getProject());
        doc.addField("kind", item.getKind());
        doc.addField("type", EntityName.DATAITEM.getValue());
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

    public static SolrInputDocument parser(Function item, FunctionMetadata metadata) {
        String keyGroup = getKeyGroup(item.getKind(), item.getProject(), item.getName());

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", item.getId());
        doc.addField("name", item.getName());
        doc.addField("project", item.getProject());
        doc.addField("kind", item.getKind());
        doc.addField("type", EntityName.FUNCTION.getValue());
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

    public static SolrInputDocument parser(Artifact item, ArtifactMetadata metadata) {
        String keyGroup = getKeyGroup(item.getKind(), item.getProject(), item.getName());

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", item.getId());
        doc.addField("name", item.getName());
        doc.addField("project", item.getProject());
        doc.addField("kind", item.getKind());
        doc.addField("type", EntityName.ARTIFACT.getValue());
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

//    public static SolrInputDocument parser(Run item, RunMetadata metadata) {
//        String keyGroup = getKeyGroup(item.getKind(), item.getProject(), "???");
//
//        SolrInputDocument doc = new SolrInputDocument();
//        doc.addField("id", item.getId());
//        doc.addField("name", "???"); //TODO name in run?
//        doc.addField("project", item.getProject());
//        doc.addField("kind", item.getKind());
//        doc.addField("type", EntityName.RUN.getValue());
//        doc.addField("keyGroup", keyGroup);
//
//        doc.addField("metadata.name", metadata.getName());
//        doc.addField("metadata.description", metadata.getDescription());
//        doc.addField("metadata.version", metadata.getVersion());
//        doc.addField("metadata.created", metadata.getCreated());
//        doc.addField("metadata.updated", metadata.getUpdated());
//        doc.addField("metadata.labels", metadata.getLabels());
//
//        return doc;
//    }

    //	public static SolrInputDocument parser(Task item, TaskMetadata metadata) {
    //		String keyGroup = getKeyGroup(item.getKind(), item.getProject(), "???");
    //
    //		SolrInputDocument doc = new SolrInputDocument();
    //		doc.addField("id", item.getId());
    //		doc.addField("name", "???"); //TODO name in task?
    //		doc.addField("project", item.getProject());
    //		doc.addField("kind", item.getKind());
    //		doc.addField("type", "task");
    //		doc.addField("keyGroup", keyGroup);
    //
    //		doc.addField("metadata.name", metadata.getName());
    //		doc.addField("metadata.description", metadata.getDescription());
    //		doc.addField("metadata.version", metadata.getVersion());
    //		doc.addField("metadata.created", metadata.getCreated());
    //		doc.addField("metadata.updated", metadata.getUpdated());
    //		doc.addField("metadata.labels", metadata.getLabels());
    //
    //		return doc;
    //	}

//    public static SolrInputDocument parser(Secret item, SecretMetadata metadata) {
//        String keyGroup = getKeyGroup(item.getKind(), item.getProject(), item.getName());
//
//        SolrInputDocument doc = new SolrInputDocument();
//        doc.addField("id", item.getId());
//        doc.addField("name", item.getName());
//        doc.addField("project", item.getProject());
//        doc.addField("kind", item.getKind());
//        doc.addField("type", EntityName.SECRET.getValue());
//        doc.addField("keyGroup", keyGroup);
//
//        doc.addField("metadata.name", metadata.getName());
//        doc.addField("metadata.description", metadata.getDescription());
//        doc.addField("metadata.project", metadata.getProject());
//        doc.addField("metadata.version", metadata.getVersion());
//        doc.addField("metadata.created", metadata.getCreated());
//        doc.addField("metadata.updated", metadata.getUpdated());
//        doc.addField("metadata.labels", metadata.getLabels());
//
//        return doc;
//    }

//    public static SolrInputDocument parser(Workflow item, WorkflowMetadata metadata) {
//        String keyGroup = getKeyGroup(item.getKind(), item.getProject(), item.getName());
//
//        SolrInputDocument doc = new SolrInputDocument();
//        doc.addField("id", item.getId());
//        doc.addField("name", item.getName());
//        doc.addField("project", item.getProject());
//        doc.addField("kind", item.getKind());
//        doc.addField("type", EntityName.WORKFLOW.getValue());
//        doc.addField("keyGroup", keyGroup);
//
//        doc.addField("metadata.name", metadata.getName());
//        doc.addField("metadata.description", metadata.getDescription());
//        doc.addField("metadata.project", metadata.getProject());
//        doc.addField("metadata.version", metadata.getVersion());
//        doc.addField("metadata.created", metadata.getCreated());
//        doc.addField("metadata.updated", metadata.getUpdated());
//        doc.addField("metadata.labels", metadata.getLabels());
//
//        return doc;
//    }
}
