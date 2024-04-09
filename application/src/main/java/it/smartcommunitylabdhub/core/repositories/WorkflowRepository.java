package it.smartcommunitylabdhub.core.repositories;

import it.smartcommunitylabdhub.core.models.entities.WorkflowEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkflowRepository
    extends JpaRepository<WorkflowEntity, String>, JpaSpecificationExecutor<WorkflowEntity> {
    List<WorkflowEntity> findByProject(String project);

    Page<WorkflowEntity> findAll(Pageable pageable);

    ////////////////////////////
    // CONTEXT SPECIFIC QUERY //
    ////////////////////////////

    @Query(
        "SELECT a FROM WorkflowEntity a WHERE a.project = :project AND (a.name, a.project, a.created) IN " +
        "(SELECT a2.name, a2.project, MAX(a2.created) FROM WorkflowEntity a2 WHERE a2.project = :project GROUP BY a2.name, a2.project) " +
        "ORDER BY a.created DESC"
    )
    List<WorkflowEntity> findAllLatestWorkflowsByProject(@Param("project") String project);

    Optional<WorkflowEntity> findByProjectAndNameAndId(
        @Param("project") String project,
        @Param("name") String name,
        @Param("id") String id
    );

    @Query(
        "SELECT a FROM WorkflowEntity a WHERE a.project = :project AND a.name = :name " +
        "AND a.created = (SELECT MAX(a2.created) FROM WorkflowEntity a2 WHERE a2.project = :project AND a2.name = :name)"
    )
    Optional<WorkflowEntity> findLatestWorkflowByProjectAndName(
        @Param("project") String project,
        @Param("name") String name
    );

    boolean existsByProjectAndNameAndId(String project, String name, String id);

    @Modifying
    @Query("DELETE FROM WorkflowEntity a WHERE a.project = :project AND a.name = :name AND a.id = :id")
    void deleteByProjectAndNameAndId(
        @Param("project") String project,
        @Param("name") String name,
        @Param("id") String id
    );

    boolean existsByProjectAndName(String project, String name);

    @Modifying
    @Query("DELETE FROM WorkflowEntity a WHERE a.project = :project AND a.name = :name ")
    void deleteByProjectAndName(@Param("project") String project, @Param("name") String name);

    @Modifying
    @Query("DELETE FROM WorkflowEntity a WHERE a.project = :project ")
    void deleteByProjectName(@Param("project") String project);
}
