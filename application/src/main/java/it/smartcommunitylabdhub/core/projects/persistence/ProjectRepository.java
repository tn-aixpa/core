package it.smartcommunitylabdhub.core.projects.persistence;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository
    extends JpaRepository<ProjectEntity, String>, JpaSpecificationExecutor<ProjectEntity> {
    Boolean existsByName(String name);

    @Modifying
    @Query("DELETE FROM ProjectEntity p WHERE p.name = :name")
    void deleteByName(@Param("name") String name);

    Optional<ProjectEntity> findByName(String name);

    @NotNull
    Page<ProjectEntity> findAll(@NotNull Pageable pageable);
}
