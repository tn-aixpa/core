package it.smartcommunitylabdhub.files.service;

import java.util.Map;

import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import jakarta.validation.constraints.NotNull;

public interface FilesStore {
    String downloadAsUrl(@NotNull String path);

    //TODO
    // InputStream downloadAsStream(@NotNull String path);

    Map<String, FileInfo> readMetadata(@NotNull String path);
}
