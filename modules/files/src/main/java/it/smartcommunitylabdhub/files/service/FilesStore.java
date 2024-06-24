package it.smartcommunitylabdhub.files.service;

import it.smartcommunitylabdhub.commons.models.base.DownloadInfo;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.base.UploadInfo;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface FilesStore {
    DownloadInfo downloadAsUrl(@NotNull String path);
    
    UploadInfo uploadAsUrl(@NotNull String entityType, @NotNull String projectId, @NotNull String entityId, @NotNull String filename);
    
    UploadInfo startUpload(@NotNull String entityType, @NotNull String projectId, @NotNull String entityId, @NotNull String filename);
    
    UploadInfo uploadPart(@NotNull String path, @NotNull String uploadId, @NotNull Integer partNumber);
    
    UploadInfo completeUpload(@NotNull String path, @NotNull String uploadId, @NotNull List<String> eTagPartList);
    
    //TODO
    // InputStream downloadAsStream(@NotNull String path);

    List<FileInfo> readMetadata(@NotNull String path);
}
