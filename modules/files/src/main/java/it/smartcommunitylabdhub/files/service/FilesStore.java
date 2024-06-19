package it.smartcommunitylabdhub.files.service;

import it.smartcommunitylabdhub.commons.models.base.DownloadInfo;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.base.UploadInfo;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface FilesStore {
    DownloadInfo downloadAsUrl(@NotNull String path);
    
    UploadInfo uploadAsUrl(@NotNull String entityType, @NotNull String projectId, @NotNull String entityId, @NotNull String filename);
    
    //TODO
    // InputStream downloadAsStream(@NotNull String path);

    List<FileInfo> readMetadata(@NotNull String path);
}
