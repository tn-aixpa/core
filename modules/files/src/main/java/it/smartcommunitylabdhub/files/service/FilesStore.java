package it.smartcommunitylabdhub.files.service;

import it.smartcommunitylabdhub.commons.models.base.DownloadInfo;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface FilesStore {
    DownloadInfo downloadAsUrl(@NotNull String path);

    //TODO
    // InputStream downloadAsStream(@NotNull String path);

    List<FileInfo> readMetadata(@NotNull String path);
}
