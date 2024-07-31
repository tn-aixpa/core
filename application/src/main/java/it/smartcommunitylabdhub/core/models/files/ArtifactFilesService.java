package it.smartcommunitylabdhub.core.models.files;

import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.base.DownloadInfo;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.base.UploadInfo;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.lang.Nullable;

public interface ArtifactFilesService {
    public List<FileInfo> getFileInfo(@NotNull String id) throws NoSuchEntityException, SystemException;

    public void storeFileInfo(@NotNull String id, List<FileInfo> files) throws SystemException;
    
    public DownloadInfo downloadFileAsUrl(@NotNull String id) throws NoSuchEntityException, SystemException;

    public UploadInfo uploadFileAsUrl(@Nullable String id, @NotNull String filename)
        throws NoSuchEntityException, SystemException;

    public UploadInfo startMultiPartUpload(@Nullable String id, @NotNull String filename)
        throws NoSuchEntityException, SystemException;

    public UploadInfo uploadMultiPart(
        @Nullable String id,
        @NotNull String filename,
        @NotNull String uploadId,
        @NotNull Integer partNumber
    ) throws NoSuchEntityException, SystemException;

    public UploadInfo completeMultiPartUpload(
        @Nullable String id,
        @NotNull String filename,
        @NotNull String uploadId,
        @NotNull List<String> eTagPartList
    ) throws NoSuchEntityException, SystemException;
}
