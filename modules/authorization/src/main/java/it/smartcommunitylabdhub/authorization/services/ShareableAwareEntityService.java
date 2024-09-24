package it.smartcommunitylabdhub.authorization.services;

import it.smartcommunitylabdhub.authorization.model.ResourceShareEntity;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import jakarta.validation.constraints.NotNull;

public interface ShareableAwareEntityService<T extends BaseDTO> {
    public ResourceShareEntity share(@NotNull String id, @NotNull String user);

    public ResourceShareEntity revoke(@NotNull String id, @NotNull String user);
}
