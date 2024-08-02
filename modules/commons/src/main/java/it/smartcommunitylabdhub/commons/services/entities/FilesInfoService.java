package it.smartcommunitylabdhub.commons.services.entities;

import java.util.List;

import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.entities.files.FilesInfo;
import jakarta.validation.constraints.NotNull;

public interface FilesInfoService {
    public FilesInfo getFilesInfo(@NotNull String entityName, @NotNull String entityId) throws SystemException;

    public FilesInfo saveFilesInfo(@NotNull String entityName, @NotNull String entityId, List<FileInfo> files)
        throws SystemException;

}
