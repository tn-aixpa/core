package it.smartcommunitylabdhub.files.service;

import java.io.Serializable;
import java.util.Map;

import jakarta.validation.constraints.NotNull;

public interface FilesStore {
    String downloadAsUrl(@NotNull String path);

    //TODO
    // InputStream downloadAsStream(@NotNull String path);

    Map<String, Serializable> readMetadata(@NotNull String path);
}
