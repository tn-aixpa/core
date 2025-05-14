package it.smartcommunitylabdhub.core.relationships.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface RelationshipRepository
    extends JpaRepository<RelationshipEntity, String>, JpaSpecificationExecutor<RelationshipEntity> {
    @Query(
        "SELECT r FROM RelationshipEntity r WHERE r.project=:project " +
        " AND ((r.sourceId=:entityId  AND r.sourceType=:type) OR (r.destId=:entityId  AND r.destType=:type))"
    )
    List<RelationshipEntity> findByProjectAndEntityId(String project, String type, String entityId);

    @Query(
        "SELECT r FROM RelationshipEntity r WHERE r.project=:project " +
        " AND r.sourceId=:entityId  AND r.sourceType=:type"
    )
    List<RelationshipEntity> findByProjectAndSourceEntityId(String project, String type, String entityId);

    @Query("SELECT r FROM RelationshipEntity r WHERE r.project=:project AND (r.sourceType=:type OR r.destType=:type)")
    List<RelationshipEntity> findByProjectAndEntity(String project, String type);

    List<RelationshipEntity> findByProject(String project);
}
