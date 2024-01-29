package it.smartcommunitylabdhub.core.repositories;

import it.smartcommunitylabdhub.core.models.entities.runnable.RunnableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RunnableRepository extends JpaRepository<RunnableEntity, String> {
}
