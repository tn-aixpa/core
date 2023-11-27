package it.smartcommunitylabdhub.core.repositories;

import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogRepository extends JpaRepository<LogEntity, String> {

    List<LogEntity> findByProject(String name);

    List<LogEntity> findByRun(String uuid);

    @Modifying
    @Query("DELETE FROM LogEntity l WHERE l.project = :project ")
    void deleteByProjectName(@Param("project") String project);
}
