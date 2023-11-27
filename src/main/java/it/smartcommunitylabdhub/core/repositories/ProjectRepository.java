package it.smartcommunitylabdhub.core.repositories;

import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<ProjectEntity, String> {

    Boolean existsByName(String name);

    @Modifying
    @Query("DELETE FROM ProjectEntity p WHERE p.name = :name")
    void deleteByName(@Param("name") String name);

    Optional<ProjectEntity> findByName(String name);

    Page<ProjectEntity> findAll(Pageable pageable);
}
