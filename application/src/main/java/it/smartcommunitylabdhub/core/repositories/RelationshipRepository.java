package it.smartcommunitylabdhub.core.repositories;

import it.smartcommunitylabdhub.core.models.entities.RelationshipEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface RelationshipRepository
    extends JpaRepository<RelationshipEntity, String>, JpaSpecificationExecutor<RelationshipEntity> {
    @Query(
        "SELECT r FROM RelationshipEntity r WHERE r.project=:project AND (r.sourceId=:entityId OR r.destId=:entityId) AND (r.sourceType=:type OR r.destType=:type)"
    )
    List<RelationshipEntity> findByProjectAndEntityId(String project, String type, String entityId);

    @Query("SELECT r FROM RelationshipEntity r WHERE r.project=:project AND (r.sourceType=:type OR r.destType=:type)")
    List<RelationshipEntity> findByProjectAndEntity(String project, String type);

    List<RelationshipEntity> findByProject(String project);
}
