package it.smartcommunitylabdhub.core.repositories;

import it.smartcommunitylabdhub.core.models.entities.FunctionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FunctionRepository
    extends JpaRepository<FunctionEntity, String>, JpaSpecificationExecutor<FunctionEntity> {
    List<FunctionEntity> findByProject(String project);

    @Query(
        "SELECT a FROM FunctionEntity a WHERE a.project = :project AND (a.name, a.project, a.created) IN " +
        "(SELECT a2.name, a2.project, MAX(a2.created) FROM FunctionEntity a2 WHERE a2.project = :project GROUP BY a2.name, a2.project) " +
        "ORDER BY a.created DESC"
    )
    List<FunctionEntity> findAllLatestFunctionsByProject(@Param("project") String project);

    Page<FunctionEntity> findAll(Pageable pageable);

    @Query(
        "SELECT a FROM FunctionEntity a WHERE (a.name, a.created) IN " +
        "(SELECT a2.name, MAX(a2.created) FROM FunctionEntity a2 GROUP BY a2.name) " +
        "ORDER BY a.created DESC"
    )
    List<FunctionEntity> findAllLatestFunctions();

    ////////////////////////////
    // CONTEXT SPECIFIC QUERY //
    ////////////////////////////

    List<FunctionEntity> findAllByProjectAndNameOrderByCreatedDesc(String project, String name);

    Optional<FunctionEntity> findByProjectAndNameAndId(
        @Param("project") String project,
        @Param("name") String name,
        @Param("id") String id
    );

    @Query(
        "SELECT a FROM FunctionEntity a WHERE a.project = :project AND a.name = :name " +
        "AND a.created = (SELECT MAX(a2.created) FROM FunctionEntity a2 WHERE a2.project = :project AND a2.name = :name)"
    )
    Optional<FunctionEntity> findLatestFunctionByProjectAndName(
        @Param("project") String project,
        @Param("name") String name
    );

    @Query(
        "SELECT a FROM FunctionEntity a WHERE a.project = :project AND a.id = :id " +
        "AND a.created = (SELECT MAX(a2.created) FROM FunctionEntity a2 WHERE a2.project = :project AND a2.id = :id)"
    )
    Optional<FunctionEntity> findLatestFunctionByProjectAndId(@Param("project") String project, @Param("id") String id);

    boolean existsByProjectAndNameAndId(String project, String name, String id);

    @Modifying
    @Query("DELETE FROM FunctionEntity a WHERE a.project = :project AND a.name = :name AND a.id = :id")
    void deleteByProjectAndNameAndId(
        @Param("project") String project,
        @Param("name") String name,
        @Param("id") String id
    );

    boolean existsByProjectAndName(String project, String name);

    @Modifying
    @Query("DELETE FROM FunctionEntity a WHERE a.project = :project AND a.name = :name ")
    void deleteByProjectAndName(@Param("project") String project, @Param("name") String name);

    @Modifying
    @Query("DELETE FROM FunctionEntity a WHERE a.project = :project ")
    void deleteByProjectName(@Param("project") String project);
}
