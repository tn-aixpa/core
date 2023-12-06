package it.smartcommunitylabdhub.core.repositories;

import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RunRepository extends JpaRepository<RunEntity, String> {

    List<RunEntity> findByProject(String uuid);

    List<RunEntity> findByTask(String task);

    @Modifying
    @Query("DELETE FROM RunEntity r WHERE r.project = :project ")
    void deleteByProjectName(@Param("project") String project);

    @Modifying
    @Query("DELETE FROM RunEntity r WHERE r.taskId = :taskId ")
    void deleteByTaskId(String taskId);
}
