package it.smartcommunitylabdhub.files.service;

import jakarta.validation.constraints.NotNull;

/**
 * Support download/upload via URLs for entities
 */

public interface EntityFilesService {
    public String getDownloadUrl(@NotNull String id);

    public String getUploadUrl(@NotNull String id);
}
