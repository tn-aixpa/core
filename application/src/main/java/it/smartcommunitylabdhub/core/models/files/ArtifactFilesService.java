package it.smartcommunitylabdhub.core.models.files;

import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import jakarta.validation.constraints.NotNull;

public interface ArtifactFilesService {
    public String downloadArtifactAsUrl(@NotNull String id) throws NoSuchEntityException, SystemException;
}
