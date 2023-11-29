package it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces;

import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface FunctionFieldAccessor<O extends FunctionFieldAccessor<O>> extends CommonFieldAccessor<O> {

    // COMMENT: those below are METADATA function common fields
    // name:String, tag:String, project:String, hash:String, update:Date,
    // labels:Map<String,String>, categories:List<String>

    // HACK: test if this work or not
    @SuppressWarnings("unchecked")
    default Map<String, String> getLabels() {
        return mapHasField(getMetadata(), "labels") ? (Map<String, String>) getMetadata().get("labels") : null;
    }

    @SuppressWarnings("unchecked")
    default List<String> getCategories() {
        return mapHasField(getMetadata(), "categories") ? (List<String>) getMetadata().get("categories") : null;
    }

    default String getName() {
        return mapHasField(getMetadata(), "name") ? (String) getMetadata().get("name") : null;
    }

    default String getTag() {
        return mapHasField(getMetadata(), "tag") ? (String) getMetadata().get("tag") : null;
    }

    default String getProject() {
        return mapHasField(getMetadata(), "project") ? (String) getMetadata().get("project") : null;
    }

    default String getHash() {
        return mapHasField(getMetadata(), "hash") ? (String) getMetadata().get("hash") : null;
    }

    default Date getUpdate() {
        return mapHasField(getMetadata(), "update") ? ConversionUtils.convert(
                getMetadata().get("update"), "datetime") : null;
    }

    // COMMENT: those below are SPEC fuction common fields
    // command:string,
    // args:List<String>????,
    // build: Map<String, Object> -> {..... , commands:List<String>}
    // image:string,
    // description:string ,
    // volumes:List<Map<String, Object>> ->
    // [{name:{'secret':{'secretName':"Username"}}}],
    // volume_mounts:List<Map<String, String>>,
    // env:List<Map<String, String>>

    default String getCommand() {
        return mapHasField(getSpecs(), "command") ? (String) getSpecs().get("command") : null;
    }

    @SuppressWarnings("unchecked")
    default List<String> getArgs() {
        return mapHasField(getSpecs(), "args") ? (List<String>) getSpecs().get("args") : null;
    }

    @SuppressWarnings("unchecked")
    default Map<String, Object> getBuild() {
        return mapHasField(getSpecs(), "build") ? (Map<String, Object>) getSpecs().get("build") : null;
    }

    default String getImage() {
        return mapHasField(getSpecs(), "image") ? (String) getSpecs().get("image") : null;
    }

    default String getDescription() {
        return mapHasField(getSpecs(), "description") ? (String) getSpecs().get("description") : null;
    }

    default String getDefaultHandler() {
        return mapHasField(getSpecs(), "default_handler") ? (String) getSpecs().get("default_handler") : null;
    }

    @SuppressWarnings("unchecked")
    default List<Map<String, Object>> getVolumes() {
        return mapHasField(getSpecs(), "volumes") ? (List<Map<String, Object>>) getSpecs().get("volumes") : null;
    }

    @SuppressWarnings("unchecked")
    default List<Map<String, Object>> getVolumeMounts() {
        return mapHasField(getSpecs(), "volume_mounts")
                ? (List<Map<String, Object>>) getSpecs().get("volume_mounts")
                : null;
    }

    @SuppressWarnings("unchecked")
    default List<Map<String, String>> getEnv() {
        return mapHasField(getSpecs(), "env")
                ? (List<Map<String, String>>) getSpecs().get("env")
                : null;
    }
}
