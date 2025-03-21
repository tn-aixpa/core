package it.smartcommunitylabdhub.metrics;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MetricsRepository
    extends JpaRepository<MetricsEntity, String>, JpaSpecificationExecutor<MetricsEntity> {
    MetricsEntity findByEntityNameAndEntityIdAndName(String entityName, String entityId, String name);

    List<MetricsEntity> findByEntityNameAndEntityId(String entityName, String entityId);
}
