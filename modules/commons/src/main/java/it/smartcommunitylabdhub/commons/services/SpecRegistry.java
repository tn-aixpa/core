package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
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
    /**
     * Create an instance of a spec based on its type and configure it with data.
     *
     * @param kind The type of the spec to create.
     * @param data The data used to configure the spec.
     * @param <S>  The generic type for the spec.
     * @return An instance of the specified spec type, or null if not found or in case of errors.
     */
    <S extends Spec> S createSpec(@NotNull String kind, Map<String, Serializable> data);

    void registerSpec(@NotNull SpecType spec, @NotNull Class<? extends Spec> clazz);

    Schema getSchema(@NotNull String kind);
    Collection<Schema> getSchemas(@NotNull EntityName name, @NotNull String runtime);
    Collection<Schema> listSchemas(@NotNull EntityName name);

    public void refresh();
}
