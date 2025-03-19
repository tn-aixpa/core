package it.smartcommunitylabdhub.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.smartcommunitylabdhub.core.models.entities.MetricsEntity;

public interface MetricsRepository
    extends JpaRepository<MetricsEntity, String>, JpaSpecificationExecutor<MetricsEntity> {
	
	MetricsEntity findByEntityNameAndEntityIdAndName(String entityName, String entityId, String name);
	
	List<MetricsEntity> findByEntityNameAndEntityId(String entityName, String entityId);
}
