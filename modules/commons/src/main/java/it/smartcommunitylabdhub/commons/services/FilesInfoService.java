package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.files.FileInfo;
import it.smartcommunitylabdhub.commons.models.files.FilesInfo;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface FilesInfoService {
    public FilesInfo getFilesInfo(@NotNull String entityName, @NotNull String entityId)
        throws StoreException, SystemException;

    public FilesInfo saveFilesInfo(@NotNull String entityName, @NotNull String entityId, List<FileInfo> files)
        throws StoreException, SystemException;

    public void clearFilesInfo(@NotNull String entityName, @NotNull String entityId)
        throws StoreException, SystemException;
}
