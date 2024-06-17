package it.smartcommunitylabdhub.core.models.files;

import java.util.Map;

import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import jakarta.validation.constraints.NotNull;

public interface ArtifactFilesService {
    public String downloadArtifactAsUrl(@NotNull String id) throws NoSuchEntityException, SystemException;
    
    public Map<String, FileInfo> getObjectMetadata(@NotNull String id) throws NoSuchEntityException, SystemException;
}
