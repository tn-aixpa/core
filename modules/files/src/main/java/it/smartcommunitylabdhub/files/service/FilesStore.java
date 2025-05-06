package it.smartcommunitylabdhub.files.service;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.infrastructure.Credentials;
import it.smartcommunitylabdhub.commons.models.files.FileInfo;
import it.smartcommunitylabdhub.files.models.DownloadInfo;
import it.smartcommunitylabdhub.files.models.UploadInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface FilesStore {
    DownloadInfo downloadAsUrl(@NotNull String path, @Nullable List<Credentials> credentials) throws StoreException;

    UploadInfo uploadAsUrl(@NotNull String path, @Nullable List<Credentials> credentials) throws StoreException;

    UploadInfo startMultiPartUpload(@NotNull String path, @Nullable List<Credentials> credentials)
        throws StoreException;

    UploadInfo uploadMultiPart(
        @NotNull String path,
        @NotNull String uploadId,
        @NotNull Integer partNumber,
        @Nullable List<Credentials> credentials
    ) throws StoreException;

    UploadInfo completeMultiPartUpload(
        @NotNull String path,
        @NotNull String uploadId,
        @NotNull List<String> partList,
        @Nullable List<Credentials> credentials
    ) throws StoreException;

    //TODO
    // InputStream downloadAsStream(@NotNull String path);

    List<FileInfo> fileInfo(@NotNull String path, @Nullable List<Credentials> credentials) throws StoreException;

    void remove(@NotNull String path, @Nullable List<Credentials> credentials) throws StoreException;
}
