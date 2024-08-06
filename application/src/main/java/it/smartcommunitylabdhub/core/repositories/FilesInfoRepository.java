package it.smartcommunitylabdhub.core.repositories;

<<<<<<< HEAD
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.smartcommunitylabdhub.core.models.entities.FilesInfoEntity;

public interface FilesInfoRepository extends JpaRepository<FilesInfoEntity, String>, JpaSpecificationExecutor<FilesInfoEntity> {
=======
import it.smartcommunitylabdhub.core.models.entities.FilesInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FilesInfoRepository
    extends JpaRepository<FilesInfoEntity, String>, JpaSpecificationExecutor<FilesInfoEntity> {
>>>>>>> origin/file_info_repo
    FilesInfoEntity findByEntityNameAndEntityId(String entityName, String entityId);
}
