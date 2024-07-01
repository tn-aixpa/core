package it.smartcommunitylabdhub.files.service;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.base.DownloadInfo;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.base.UploadInfo;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface FilesStore {
    DownloadInfo downloadAsUrl(@NotNull String path) throws StoreException;

    UploadInfo uploadAsUrl(@NotNull String path) throws StoreException;

    UploadInfo startMultiPartUpload(@NotNull String path) throws StoreException;

    UploadInfo uploadMultiPart(@NotNull String path, @NotNull String uploadId, @NotNull Integer partNumber)
        throws StoreException;

    UploadInfo completeMultiPartUpload(@NotNull String path, @NotNull String uploadId, @NotNull List<String> partList)
        throws StoreException;

    //TODO
    // InputStream downloadAsStream(@NotNull String path);

    List<FileInfo> fileInfo(@NotNull String path) throws StoreException;
}
