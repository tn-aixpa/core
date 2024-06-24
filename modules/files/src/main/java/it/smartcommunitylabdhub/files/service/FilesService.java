package it.smartcommunitylabdhub.files.service;

import it.smartcommunitylabdhub.commons.models.base.DownloadInfo;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.base.UploadInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 *
 * Path-aware files service with support for multiple backends
 */

@Service
@Slf4j
public class FilesService {

    private final Map<String, FilesStore> stores = new HashMap<>();

    public void registerStore(String prefix, FilesStore store) {
        Assert.hasText(prefix, "prefix is required to match paths");
        Assert.notNull(store, "store can not be null");

        if (stores.containsKey(prefix)) {
            throw new IllegalArgumentException("prefix is already handled by another store");
        }

        //register
        log.debug("register store for {}: {}", prefix, store.getClass().getName());
        stores.put(prefix, store);
    }

    public @Nullable FilesStore getStore(@NotNull String path) {
        //match longest prefix in keys
        int count = 0;
        FilesStore match = null;
        for (Map.Entry<String, FilesStore> entry : stores.entrySet()) {
            if (path.startsWith(entry.getKey()) && entry.getKey().length() > count) {
                count = entry.getKey().length();
                match = entry.getValue();
            }
        }

        return match;
    }

    public @Nullable DownloadInfo getDownloadAsUrl(@NotNull String path) {
        Assert.hasText(path, "path can not be null or empty");

        log.debug("resolve store for {}", path);
        //try resolving path via stores
        FilesStore store = getStore(path);
        if (store == null) {
            log.debug("no store found.");
            return null;
        }

        log.debug("found store {}", store.getClass().getName());

        DownloadInfo info = store.downloadAsUrl(path);

        if (log.isTraceEnabled()) {
            log.trace("path resolved to download url {}", info);
        }

        return info;
    }

    public @Nullable InputStream getDownloadAsStream(@NotNull String path) {
        throw new UnsupportedOperationException();
    }

    public List<FileInfo> getObjectMetadata(@NotNull String path) {
        Assert.hasText(path, "path can not be null or empty");

        log.debug("resolve store for {}", path);
        //try resolving path via stores
        FilesStore store = getStore(path);
        if (store == null) {
            log.debug("no store found.");
            return null;
        }

        log.debug("found store {}", store.getClass().getName());

        List<FileInfo> metadata = store.readMetadata(path);

        if (log.isTraceEnabled()) {
            log.trace("path resolved to metadata {}", metadata);
        }

        return metadata;
    }
    
    public @Nullable UploadInfo getUploadAsUrl(@NotNull String entityType, @NotNull String projectId, @NotNull String entityId, @NotNull String filename) {
    	Assert.hasText(entityType, "entity can not be null or empty");
    	Assert.hasText(projectId, "projectId can not be null or empty");
        Assert.hasText(entityId, "entityId can not be null or empty");
        Assert.hasText(filename, "filename can not be null or empty");

        //try resolving path via stores
        FilesStore store = stores.get("s3://");
        if (store == null) {
            log.debug("no store found.");
            return null;
        }

        log.debug("found store {}", store.getClass().getName());

        UploadInfo info = store.uploadAsUrl(entityType, projectId, entityId, filename);

        if (log.isTraceEnabled()) {
            log.trace("path resolved to upload url {}", info);
        }

        return info;    	
    }
    
    public @Nullable UploadInfo startUpload(@NotNull String entityType, @NotNull String projectId, @NotNull String entityId, @NotNull String filename) {
    	Assert.hasText(entityType, "entity can not be null or empty");
    	Assert.hasText(projectId, "projectId can not be null or empty");
        Assert.hasText(entityId, "entityId can not be null or empty");
        Assert.hasText(filename, "filename can not be null or empty");
    	
        FilesStore store = stores.get("s3://");
        if (store == null) {
            log.debug("no store found.");
            return null;
        }
        
        log.debug("found store {}", store.getClass().getName());
        
        UploadInfo info = store.startUpload(entityType, projectId, entityId, filename);
        
        if (log.isTraceEnabled()) {
            log.trace("path resolved to start upload url {}", info);
        }
        
        return info;
    }
    
    public @Nullable UploadInfo uploadPart(@NotNull String path, @NotNull String uploadId, @NotNull Integer partNumber) {
    	Assert.hasText(path, "path can not be null or empty");
    	Assert.hasText(uploadId, "uploadId can not be null or empty");
        Assert.notNull(partNumber, "partNumber can not be null or empty");
    	
        FilesStore store = stores.get("s3://");
        if (store == null) {
            log.debug("no store found.");
            return null;
        }
        
        log.debug("found store {}", store.getClass().getName());
        
        UploadInfo info = store.uploadPart(path, uploadId, partNumber);
        
        if (log.isTraceEnabled()) {
            log.trace("path resolved to part upload url {}", info);
        }
        
        return info;    	
    }
    
    public @Nullable UploadInfo completeUpload(@NotNull String path, @NotNull String uploadId, @NotNull List<String> eTagPartList) {
    	Assert.hasText(path, "path can not be null or empty");
    	Assert.hasText(uploadId, "uploadId can not be null or empty");
        Assert.notNull(eTagPartList, "eTagPartList can not be null or empty");
    	
        FilesStore store = stores.get("s3://");
        if (store == null) {
            log.debug("no store found.");
            return null;
        }
        
        log.debug("found store {}", store.getClass().getName());
        
        UploadInfo info = store.completeUpload(path, uploadId, eTagPartList);
        
        if (log.isTraceEnabled()) {
            log.trace("path resolved to complete upload url {}", info);
        }
        
        return info;    	
    }
}
