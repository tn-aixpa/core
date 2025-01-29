package it.smartcommunitylabdhub.commons.accessors.fields;

import io.micrometer.common.lang.Nullable;
import it.smartcommunitylabdhub.commons.accessors.Accessor;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.files.FileInfo;
import it.smartcommunitylabdhub.commons.models.metrics.NumberOrNumberArray;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Status field common accessor
 */
public interface StatusFieldAccessor extends Accessor<Serializable> {
    default @Nullable String getState() {
        return get("state");
    }

    default @Nullable List<FileInfo> getFiles() {
        List<Map<String, Serializable>> raw = get("files");
        List<FileInfo> files = new LinkedList<>();
        if (raw != null) {
            raw.forEach(e -> {
                try {
                    FileInfo f = JacksonMapper.OBJECT_MAPPER.convertValue(e, FileInfo.class);
                    files.add(f);
                } catch (IllegalArgumentException ex) {
                    //skip
                }
            });
            return files;
        }
        return null;
    }
    
    default @Nullable Map<String, NumberOrNumberArray> getMetrics() {
    	Map<String, NumberOrNumberArray> result = new HashMap<>();
    	Map<String, Serializable> raw = get("metrics");
    	if(raw != null) {
    		TypeReference<NumberOrNumberArray> typeRef = new TypeReference<>() {};
    		for (Map.Entry<String, Serializable> entry : raw.entrySet()) {
    			try {
    				NumberOrNumberArray data = JacksonMapper.OBJECT_MAPPER.convertValue(entry.getValue(), typeRef);
    				result.put(entry.getKey(), data);
				} catch (Exception e) {
				}
    		}
    		return result;
    	}
    	return null;
    }

    static StatusFieldAccessor with(Map<String, Serializable> map) {
        return () -> map;
    }
}
