package it.smartcommunitylabdhub.core.repositories;

import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArtifactRepository extends JpaRepository<ArtifactEntity, String> {
    List<ArtifactEntity> findByProject(String project);

    Page<ArtifactEntity> findAll(Pageable pageable);

    ////////////////////////////
    // CONTEXT SPECIFIC QUERY //
    ////////////////////////////

    Page<ArtifactEntity> findAllByProjectAndNameOrderByCreatedDesc(String project, String name,
                                                                   Pageable pageable);

    @Query("SELECT a FROM ArtifactEntity a WHERE a.project = :project AND (a.name, a.project, a.created) IN "
            +
            "(SELECT a2.name, a2.project, MAX(a2.created) FROM ArtifactEntity a2 WHERE a2.project = :project GROUP BY a2.name, a2.project) "
            +
            "ORDER BY a.created DESC")
    Page<ArtifactEntity> findAllLatestArtifactsByProject(@Param("project") String project,
                                                         Pageable pageable);

    @Query("SELECT a FROM ArtifactEntity a WHERE a.project = :project AND (a.name, a.project, a.created) IN "
            +
            "(SELECT a2.name, a2.project, MAX(a2.created) FROM ArtifactEntity a2 WHERE a2.project = :project GROUP BY a2.name, a2.project) "
            +
            "ORDER BY a.created DESC")
    List<ArtifactEntity> findAllLatestArtifactsByProject(@Param("project") String project);

    Optional<ArtifactEntity> findByProjectAndNameAndId(@Param("project") String project,
                                                       @Param("name") String name,
                                                       @Param("id") String id);

    @Query("SELECT a FROM ArtifactEntity a WHERE a.project = :project AND a.name = :name " +
            "AND a.created = (SELECT MAX(a2.created) FROM ArtifactEntity a2 WHERE a2.project = :project AND a2.name = :name)")
    Optional<ArtifactEntity> findLatestArtifactByProjectAndName(@Param("project") String project,
                                                                @Param("name") String name);

    boolean existsByProjectAndNameAndId(String project, String name, String id);

    @Modifying
    @Query("DELETE FROM ArtifactEntity a WHERE a.project = :project AND a.name = :name AND a.id = :id")
    void deleteByProjectAndNameAndId(@Param("project") String project,
                                     @Param("name") String name,
                                     @Param("id") String id);

    boolean existsByProjectAndName(String project, String name);

    @Modifying
    @Query("DELETE FROM ArtifactEntity a WHERE a.project = :project AND a.name = :name ")
    void deleteByProjectAndName(@Param("project") String project, @Param("name") String name);

    @Modifying
    @Query("DELETE FROM ArtifactEntity a WHERE a.project = :project ")
    void deleteByProjectName(@Param("project") String project);

}
