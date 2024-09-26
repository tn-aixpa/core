package it.smartcommunitylabdhub.authorization.services;

import it.smartcommunitylabdhub.authorization.model.ResourceShareEntity;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface ShareableAwareEntityService<T extends BaseDTO> {
    public List<ResourceShareEntity> listSharesById(@NotNull String id);

    public ResourceShareEntity share(@NotNull String id, @NotNull String user);

    public void revoke(@NotNull String id, @NotNull String user);
}
