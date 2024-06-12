package it.smartcommunitylabdhub.files.service;

import jakarta.validation.constraints.NotNull;

public interface FilesStore {
    String downloadAsUrl(@NotNull String path);

    //TODO
    // InputStream downloadAsStream(@NotNull String path);

    //TODO
    // Map<String, Serializable> readMetadata(@NotNull String path);
}
