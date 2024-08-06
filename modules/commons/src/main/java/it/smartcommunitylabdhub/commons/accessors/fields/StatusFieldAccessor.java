package it.smartcommunitylabdhub.commons.accessors.fields;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.micrometer.common.lang.Nullable;
import it.smartcommunitylabdhub.commons.accessors.Accessor;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
<<<<<<< HEAD
=======
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
>>>>>>> origin/file_info_repo

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
                FileInfo f = JacksonMapper.OBJECT_MAPPER.convertValue(e, FileInfo.class);
                files.add(f);
            });
            return files;
        }
        return null;
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

    static StatusFieldAccessor with(Map<String, Serializable> map) {
        return () -> map;
    }
}
