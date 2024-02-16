package it.smartcommunitylabdhub.core.components.solr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemResult {
	String id;
	String kind;
	String project;
	String name;
	String type;
	Map<String, Object> metadata = new HashMap<>();
	Map<String, List<String>> highlights = new HashMap<>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Map<String, Object> getMetadata() {
		return metadata;
	}
	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}
	public Map<String, List<String>> getHighlights() {
		return highlights;
	}
	public void setHighlights(Map<String, List<String>> highlights) {
		this.highlights = highlights;
	}
}
