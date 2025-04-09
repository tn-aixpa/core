package it.smartcommunitylabdhub.core.repositories;

import it.smartcommunitylabdhub.core.models.entities.LogEntity;
import it.smartcommunitylabdhub.core.models.entities.ReportEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<ReportEntity, String>, JpaSpecificationExecutor<ReportEntity> {
    Page<LogEntity> findByProject(String name, Pageable pageable);

    Page<LogEntity> findByEntity(String uuid, Pageable pageable);

    @Modifying
    @Query("DELETE FROM ReportEntity l WHERE l.project = :project ")
    void deleteByProjectName(@Param("project") String project);
}
