package it.smartcommunitylabdhub.core.files.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FilesInfoRepository
    extends JpaRepository<FilesInfoEntity, String>, JpaSpecificationExecutor<FilesInfoEntity> {
    FilesInfoEntity findByEntityNameAndEntityId(String entityName, String entityId);
}
