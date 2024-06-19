package it.smartcommunitylabdhub.core.models.files;

import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.base.DownloadInfo;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.base.UploadInfo;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface ArtifactFilesService {
    public DownloadInfo downloadArtifactAsUrl(@NotNull String id) throws NoSuchEntityException, SystemException;
    
    public UploadInfo uploadArtifactAsUrl(@NotNull String projectId, @NotNull String artifactId, @NotNull String filename) throws NoSuchEntityException, SystemException;

    public List<FileInfo> getObjectMetadata(@NotNull String id) throws NoSuchEntityException, SystemException;
}
