package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.base.RelationshipDetail;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface RelationshipsAwareEntityService<T extends BaseDTO> {
    List<RelationshipDetail> getRelationships(@NotNull String id);
}
