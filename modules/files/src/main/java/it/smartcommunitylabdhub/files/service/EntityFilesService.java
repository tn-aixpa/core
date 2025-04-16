package it.smartcommunitylabdhub.files.service;

import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.files.FileInfo;
import it.smartcommunitylabdhub.files.models.DownloadInfo;
import it.smartcommunitylabdhub.files.models.UploadInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface EntityFilesService<T extends BaseDTO> {
    public List<FileInfo> getFileInfo(@NotNull String id) throws NoSuchEntityException, SystemException;

    public void storeFileInfo(@NotNull String id, List<FileInfo> files) throws SystemException;

    public DownloadInfo downloadFileAsUrl(@NotNull String id) throws NoSuchEntityException, SystemException;

    public DownloadInfo downloadFileAsUrl(@NotNull String id, @NotNull String path)
        throws NoSuchEntityException, SystemException;

    public UploadInfo uploadFileAsUrl(@NotNull String project, @Nullable String id, @NotNull String filename)
        throws NoSuchEntityException, SystemException;

    public UploadInfo startMultiPartUpload(@NotNull String project, @Nullable String id, @NotNull String filename)
        throws NoSuchEntityException, SystemException;

    public UploadInfo uploadMultiPart(
        @NotNull String project,
        @Nullable String id,
        @NotNull String filename,
        @NotNull String uploadId,
        @NotNull Integer partNumber
    ) throws NoSuchEntityException, SystemException;

    public UploadInfo completeMultiPartUpload(
        @NotNull String project,
        @Nullable String id,
        @NotNull String filename,
        @NotNull String uploadId,
        @NotNull List<String> eTagPartList
    ) throws NoSuchEntityException, SystemException;
}
