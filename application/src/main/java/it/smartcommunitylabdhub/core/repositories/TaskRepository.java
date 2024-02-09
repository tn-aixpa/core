package it.smartcommunitylabdhub.core.repositories;

import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<TaskEntity, String>, JpaSpecificationExecutor<TaskEntity> {

    List<TaskEntity> findByFunction(String function);

    @Modifying
    @Query("DELETE FROM TaskEntity t WHERE t.project = :project ")
    void deleteByProjectName(@Param("project") String project);


    ////////////////////////////
    // CONTEXT SPECIFIC QUERY //
    ////////////////////////////
    

    Optional<TaskEntity> findByProjectAndId(@Param("project") String project,
                                            @Param("id") String id);


    boolean existsByProjectAndId(String project, String id);

    @Modifying
    @Query("DELETE FROM TaskEntity a WHERE a.project = :project AND a.id = :id")
    void deleteByProjectAndId(@Param("project") String project,
                              @Param("id") String id);

}
