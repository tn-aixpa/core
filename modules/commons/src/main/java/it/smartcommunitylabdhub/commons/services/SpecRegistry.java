package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.schemas.Schema;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

@Valid
public interface SpecRegistry {
    void registerSpec(
        @NotNull SpecType spec,
        @NotNull Class<? extends Spec> clazz,
        SpecFactory<? extends Spec> factory
    );

    <S extends Spec> S createSpec(@NotNull String kind, Map<String, Serializable> data);

    Schema getSchema(@NotNull String kind);
    Collection<Schema> getSchemas(@NotNull EntityName name, @NotNull String runtime);
    Collection<Schema> listSchemas(@NotNull EntityName name);
}
