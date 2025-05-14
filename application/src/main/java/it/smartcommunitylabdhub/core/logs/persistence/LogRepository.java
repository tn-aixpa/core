package it.smartcommunitylabdhub.core.logs.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LogRepository extends JpaRepository<LogEntity, String>, JpaSpecificationExecutor<LogEntity> {
    Page<LogEntity> findByProject(String name, Pageable pageable);

    Page<LogEntity> findByRun(String uuid, Pageable pageable);

    @Modifying
    @Query("DELETE FROM LogEntity l WHERE l.project = :project ")
    void deleteByProjectName(@Param("project") String project);
}
