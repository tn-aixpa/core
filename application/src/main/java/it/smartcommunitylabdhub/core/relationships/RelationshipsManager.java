package it.smartcommunitylabdhub.core.relationships;

import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.base.RelationshipDetail;
import it.smartcommunitylabdhub.commons.models.metadata.RelationshipsMetadata;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface RelationshipsManager<T extends BaseDTO> {
    List<RelationshipDetail> getRelationships(@NotNull String id);

    List<RelationshipDetail> registerRelationships(@NotNull String id, @NotNull RelationshipsMetadata relationships);

    void clearRelationships(@NotNull String id);
}
