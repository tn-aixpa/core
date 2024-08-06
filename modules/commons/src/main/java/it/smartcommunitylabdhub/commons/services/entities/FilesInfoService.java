package it.smartcommunitylabdhub.commons.services.entities;

<<<<<<< HEAD
import java.util.List;

=======
>>>>>>> origin/file_info_repo
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.exceptions.SystemException;
import it.smartcommunitylabdhub.commons.models.base.FileInfo;
import it.smartcommunitylabdhub.commons.models.entities.files.FilesInfo;
import jakarta.validation.constraints.NotNull;
<<<<<<< HEAD

public interface FilesInfoService {
    public FilesInfo getFilesInfo(@NotNull String entityName, @NotNull String entityId) throws SystemException;

    public FilesInfo saveFilesInfo(@NotNull String entityName, @NotNull String entityId, List<FileInfo> files)
        throws SystemException, StoreException;

=======
import java.util.List;

public interface FilesInfoService {
    public FilesInfo getFilesInfo(@NotNull String entityName, @NotNull String entityId)
        throws StoreException, SystemException;

    public FilesInfo saveFilesInfo(@NotNull String entityName, @NotNull String entityId, List<FileInfo> files)
        throws StoreException, SystemException;
>>>>>>> origin/file_info_repo
}
