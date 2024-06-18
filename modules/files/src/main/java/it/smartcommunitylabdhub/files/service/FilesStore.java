package it.smartcommunitylabdhub.files.service;

import java.util.List;

import it.smartcommunitylabdhub.commons.models.base.DownloadInfo;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import jakarta.validation.constraints.NotNull;

public interface FilesStore {
	DownloadInfo downloadAsUrl(@NotNull String path);

    //TODO
    // InputStream downloadAsStream(@NotNull String path);

    List<FileInfo> readMetadata(@NotNull String path);
}
