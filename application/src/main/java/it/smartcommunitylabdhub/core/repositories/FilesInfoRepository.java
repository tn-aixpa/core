package it.smartcommunitylabdhub.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.smartcommunitylabdhub.core.models.entities.FilesInfoEntity;

public interface FilesInfoRepository extends JpaRepository<FilesInfoEntity, String>, JpaSpecificationExecutor<FilesInfoEntity> {
    FilesInfoEntity findByEntityNameAndEntityId(String entityName, String entityId);
}
