package it.smartcommunitylabdhub.core.models.files;

import java.io.Serializable;
import java.util.Map;

import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import jakarta.validation.constraints.NotNull;

public interface ArtifactFilesService {
    public String downloadArtifactAsUrl(@NotNull String id) throws NoSuchEntityException, SystemException;
    
    public Map<String, Serializable> getObjectMetadata(@NotNull String id) throws NoSuchEntityException, SystemException;
}
