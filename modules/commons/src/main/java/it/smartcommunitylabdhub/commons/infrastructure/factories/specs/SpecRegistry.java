package it.smartcommunitylabdhub.commons.infrastructure.factories.specs;

import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import java.util.Map;

public interface SpecRegistry {
    /**
     * Create an instance of a spec based on its type and configure it with data.
     *
     * @param kind The type of the spec to create.
     * @param data The data used to configure the spec.
     * @param <S>  The generic type for the spec.
     * @return An instance of the specified spec type, or null if not found or in case of errors.
     */
    <S extends Spec> S createSpec(String kind, EntityName entity, Map<String, Object> data);

    <S extends Spec> S createSpec(String runtime, String kind, EntityName entity, Map<String, Object> data);

    public void registerSpecTypes(Map<String, Class<? extends Spec>> specTypeMap);
}
