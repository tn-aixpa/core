package it.smartcommunitylabdhub.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;

import java.util.List;
import java.util.Optional;

public interface SecretRepository extends JpaRepository<SecretEntity, String>, JpaSpecificationExecutor<SecretEntity> {

    List<SecretEntity> findByProject(String project);

    @Modifying
    @Query("DELETE FROM SecretEntity s WHERE s.project = :project ")
    void deleteByProjectName(@Param("project") String project);


    ////////////////////////////
    // CONTEXT SPECIFIC QUERY //
    ////////////////////////////
    

    Optional<SecretEntity> findByProjectAndId(@Param("project") String project,
                                            @Param("id") String id);


    boolean existsByProjectAndId(String project, String id);

    @Modifying
    @Query("DELETE FROM SecretEntity a WHERE a.project = :project AND a.id = :id")
    void deleteByProjectAndId(@Param("project") String project,
                              @Param("id") String id);

    boolean existsByProjectAndName(String project, String key);

    Optional<SecretEntity> findByProjectAndName(String project, String n);

}
