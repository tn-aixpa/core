package it.smartcommunitylabdhub.authorization.repositories;

import it.smartcommunitylabdhub.authorization.model.ResourceShareEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ResourceShareRepository
    extends JpaRepository<ResourceShareEntity, String>, JpaSpecificationExecutor<ResourceShareEntity> {
    List<ResourceShareEntity> findByProject(String project);
    List<ResourceShareEntity> findByOwner(String owner);
    List<ResourceShareEntity> findByUser(String user);

    List<ResourceShareEntity> findByProjectAndOwner(String project, String owner);
    List<ResourceShareEntity> findByProjectAndUser(String project, String user);

    List<ResourceShareEntity> findByProjectAndEntityAndEntityId(String project, String entity, String entityId);
    List<ResourceShareEntity> findByProjectAndEntityAndEntityIdAndUser(
        String project,
        String entity,
        String entityId,
        String user
    );
}
