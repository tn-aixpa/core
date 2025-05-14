package it.smartcommunitylabdhub.core.metrics.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MetricsRepository
    extends JpaRepository<MetricsEntity, String>, JpaSpecificationExecutor<MetricsEntity> {
    MetricsEntity findByEntityNameAndEntityIdAndName(String entityName, String entityId, String name);

    List<MetricsEntity> findByEntityNameAndEntityId(String entityName, String entityId);
}
