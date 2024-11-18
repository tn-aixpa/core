package it.smartcommunitylabdhub.authorization.services;

import it.smartcommunitylabdhub.authorization.model.ResourceShareEntity;
import it.smartcommunitylabdhub.authorization.repositories.ResourceShareRepository;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Slf4j
public class ResourceSharingService {

    private final ResourceShareRepository repository;

    public ResourceSharingService(ResourceShareRepository repository) {
        Assert.notNull(repository, "share repository can not be null");
        this.repository = repository;
    }

    public ResourceShareEntity share(
        @NotNull String project,
        @NotNull EntityName entityName,
        @NotNull String id,
        @NotNull String user
    ) throws StoreException {
        log.debug("create share for {}:{} to user {}", entityName, id, user);

        ResourceShareEntity share = ResourceShareEntity
            .builder()
            .id(UUID.randomUUID().toString().replace("-", ""))
            .project(project)
            .entity(entityName.getValue())
            .entityId(id)
            .user(user)
            .build();

        share = repository.saveAndFlush(share);

        if (log.isTraceEnabled()) {
            log.trace("share: ", share);
        }

        return share;
    }

    public ResourceShareEntity revoke(@NotNull String id) throws StoreException {
        log.debug("revoke share {}", id);
        Optional<ResourceShareEntity> res = repository.findById(id);
        if (res.isEmpty()) {
            return null;
        }

        ResourceShareEntity share = res.get();
        repository.delete(share);

        if (log.isTraceEnabled()) {
            log.trace("share: ", share);
        }

        return share;
    }

    public ResourceShareEntity get(@NotNull String id) throws StoreException {
        log.debug("get share {}", id);
        Optional<ResourceShareEntity> res = repository.findById(id);
        if (res.isEmpty()) {
            return null;
        }

        ResourceShareEntity share = res.get();
        if (log.isTraceEnabled()) {
            log.trace("share: ", share);
        }

        return share;
    }

    public List<ResourceShareEntity> listByProject(@NotNull String project) throws StoreException {
        return repository.findByProject(project);
    }

    public List<ResourceShareEntity> listByUser(@NotNull String user) throws StoreException {
        return repository.findByUser(user);
    }

    public List<ResourceShareEntity> listByOwner(@NotNull String owner) throws StoreException {
        return repository.findByOwner(owner);
    }

    public List<ResourceShareEntity> listByProjectAndOwner(@NotNull String project, @NotNull String owner)
        throws StoreException {
        return repository.findByProjectAndOwner(project, owner);
    }

    public List<ResourceShareEntity> listByProjectAndUser(@NotNull String project, @NotNull String user)
        throws StoreException {
        return repository.findByProjectAndUser(project, user);
    }

    public List<ResourceShareEntity> listByProjectAndEntity(
        @NotNull String project,
        @NotNull EntityName entity,
        @NotNull String id
    ) throws StoreException {
        return repository.findByProjectAndEntityAndEntityId(project, entity.getValue(), id);
    }

    public List<ResourceShareEntity> listByProjectAndEntity(
        @NotNull String project,
        @NotNull EntityName entity,
        @NotNull String id,
        @NotNull String user
    ) throws StoreException {
        return repository.findByProjectAndEntityAndEntityIdAndUser(project, entity.getValue(), id, user);
    }
}
