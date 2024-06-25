package it.smartcommunitylabdhub.core.models.files;

import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.base.DownloadInfo;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.base.UploadInfo;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface DataItemFilesService {
    public DownloadInfo downloadAsUrl(@NotNull String id) throws NoSuchEntityException, SystemException;
    
    public UploadInfo uploadAsUrl(@NotNull String projectId, @NotNull String artifactId, @NotNull String filename) 
    		throws NoSuchEntityException, SystemException;
    
    public UploadInfo startUpload(@NotNull String projectId, @NotNull String artifactId, @NotNull String filename) 
			throws NoSuchEntityException, SystemException;
    
    public UploadInfo uploadPart(@NotNull String path, @NotNull String uploadId, @NotNull Integer partNumber) 
			throws NoSuchEntityException, SystemException;
    
    public UploadInfo completeUpload(@NotNull String path, @NotNull String uploadId, @NotNull List<String> eTagPartList) 
			throws NoSuchEntityException, SystemException;

    public List<FileInfo> getObjectMetadata(@NotNull String id) throws NoSuchEntityException, SystemException;
}
