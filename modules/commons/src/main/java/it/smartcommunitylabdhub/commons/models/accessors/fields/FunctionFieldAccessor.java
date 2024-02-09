package it.smartcommunitylabdhub.commons.models.accessors.fields;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FunctionFieldAccessor extends CommonFieldAccessor {
  // COMMENT: those below are METADATA function common fields
  // name:String, tag:String, project:String, hash:String, update:Date,
  // labels:Map<String,String>, categories:List<String>

  // HACK: test if this work or not
  @SuppressWarnings("unchecked")
  default Map<String, String> getLabels() {
    return mapHasField(getMetadata(), "labels")
      ? (Map<String, String>) getMetadata().get("labels")
      : null;
  }

  @SuppressWarnings("unchecked")
  default List<String> getCategories() {
    return mapHasField(getMetadata(), "categories")
      ? (List<String>) getMetadata().get("categories")
      : null;
  }

  default String getName() {
    return mapHasField(getMetadata(), "name")
      ? (String) getMetadata().get("name")
      : null;
  }

  default String getTag() {
    return mapHasField(getMetadata(), "tag")
      ? (String) getMetadata().get("tag")
      : null;
  }

  default String getProject() {
    return mapHasField(getMetadata(), "project")
      ? (String) getMetadata().get("project")
      : null;
  }

  default String getHash() {
    return mapHasField(getMetadata(), "hash")
      ? (String) getMetadata().get("hash")
      : null;
  }

  default String getCommand() {
    return mapHasField(getSpecs(), "command")
      ? (String) getSpecs().get("command")
      : null;
  }

  @SuppressWarnings("unchecked")
  default List<String> getArgs() {
    return mapHasField(getSpecs(), "args")
      ? (List<String>) getSpecs().get("args")
      : null;
  }

  @SuppressWarnings("unchecked")
  default Map<String, Object> getBuild() {
    return mapHasField(getSpecs(), "build")
      ? (Map<String, Object>) getSpecs().get("build")
      : null;
  }

  default String getImage() {
    return mapHasField(getSpecs(), "image")
      ? (String) getSpecs().get("image")
      : null;
  }

  default String getDescription() {
    return mapHasField(getSpecs(), "description")
      ? (String) getSpecs().get("description")
      : null;
  }

  default String getDefaultHandler() {
    return mapHasField(getSpecs(), "default_handler")
      ? (String) getSpecs().get("default_handler")
      : null;
  }

  static FunctionFieldAccessor with(Map<String, Object> map) {
    return () -> map;
  }
}
