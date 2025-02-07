package it.smartcommunitylabdhub.core.components.lucene;

import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;

import it.smartcommunitylabdhub.core.models.indexers.ItemResult;

public class LuceneDocParser {

    @SuppressWarnings("unchecked")
    public static ItemResult parse(Document doc) {
        ItemResult item = new ItemResult();
        item.setId(doc.getField("id").stringValue());
        item.setType(doc.getField("type").stringValue());
        item.setKind(doc.getField("kind").stringValue());
        item.setProject(doc.getField("project").stringValue());
        item.setName(doc.getField("name").stringValue());
        item.getMetadata().put("name", doc.getField("metadata.name").stringValue());
//        item.getMetadata().put("description", doc.getFieldValue("metadata.description"));
//        item.getMetadata().put("project", (String) doc.getFieldValue("metadata.project"));
//        item.getMetadata().put("version", (String) doc.getFieldValue("metadata.version"));
//        item.getMetadata().put("created", (Date) doc.getFieldValue("metadata.created"));
//        item.getMetadata().put("updated", (Date) doc.getFieldValue("metadata.updated"));
//        item.getMetadata().put("labels", (List<String>) doc.getFieldValue("metadata.labels"));
        return item;
    }
}
