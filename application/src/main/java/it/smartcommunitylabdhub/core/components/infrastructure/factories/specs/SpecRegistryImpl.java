package it.smartcommunitylabdhub.core.components.infrastructure.factories.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.schemas.Schema;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.commons.utils.SchemaUtils;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.FactoryUtils;
import it.smartcommunitylabdhub.core.models.schemas.SchemaImpl;
import it.smartcommunitylabdhub.core.models.schemas.SchemaImpl.SchemaImplBuilder;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

@Component
@Slf4j
@Validated
public class SpecRegistryImpl implements SpecRegistry {

    private final Map<String, SpecType> specTypes = new HashMap<>();

    // A map to store spec types and their corresponding classes.
    private final Map<String, Class<? extends Spec>> specs = new HashMap<>();

    private final List<SpecFactory<? extends Spec>> factories;

    private final Map<String, Schema> schemas = new HashMap<>();

    public SpecRegistryImpl(List<SpecFactory<? extends Spec>> specFactories) {
        this.factories = specFactories;
    }

    @Override
    public void registerSpec(SpecType type, Class<? extends Spec> spec) {
        String kind = type.kind();
        EntityName entity = type.entity();

        if (specs.containsKey(kind)) {
            throw new IllegalArgumentException("duplicated registration for " + entity + ":" + kind);
        }

        log.debug("register spec for {}:{} with class {}", entity, kind, spec.getName());
        specTypes.put(kind, type);
        specs.put(kind, spec);

        log.debug("generate schema for spec {}:{} ", entity, kind);
        SchemaImplBuilder builder = SchemaImpl.builder().entity(entity).kind(kind).schema(SchemaUtils.schema(spec));
        if (StringUtils.hasText(type.runtime())) {
            builder.runtime(type.runtime());
        }
        SchemaImpl schema = builder.build();
        schemas.put(kind, schema);
    }

    /**
     * Create an instance of a spec based on its type and configure it with data.
     *
     * @param kind The type of the spec to create.
     * @param data The data used to configure the spec.
     * @param <S>  The generic type for the spec.
     * @return An instance of the specified spec type, or null if not found or in case of errors.
     */
    @Override
    public <S extends Spec> S createSpec(String kind, Map<String, Serializable> data) {
        // Retrieve the class associated with the specified spec type.
        Class<? extends Spec> specClass = retrieveSpec(kind);

        if (specClass == null) {
            throw new IllegalArgumentException("missing spec");
        }

        //pick a matching factory
        //TODO refactor with specType annotation when fixed
        SpecFactory<? extends Spec> specFactory = factories
            .stream()
            .filter(s -> FactoryUtils.isParamTypeMatch(s.getClass(), specClass, 0))
            .findFirst()
            .orElse(null);
        if (specFactory == null) {
            throw new IllegalArgumentException();
        }

        @SuppressWarnings("unchecked")
        S spec = (S) specFactory.create();
        if (data != null) {
            spec.configure(data);
        }
        return spec;
    }

    private Class<? extends Spec> retrieveSpec(String kind) {
        return specs.get(kind);
    }

    @Override
    public Schema getSchema(String kind) {
        Schema schema = retrieveSchema(kind);
        if (schema == null) {
            throw new IllegalArgumentException("missing schema");
        }

        return schema;
    }

    @Override
    public Collection<Schema> listSchemas(EntityName name) {
        return specTypes
            .entrySet()
            .stream()
            .filter(e -> name == e.getValue().entity())
            .map(e -> schemas.get(e.getKey()))
            .collect(Collectors.toList());
    }

    @Override
    public Collection<Schema> getSchemas(EntityName entity, String runtime) {
        return specTypes
            .entrySet()
            .stream()
            .filter(e -> entity == e.getValue().entity() && runtime.equals(e.getValue().runtime()))
            .map(e -> schemas.get(e.getKey()))
            .collect(Collectors.toList());
    }

    private Schema retrieveSchema(@NotNull String kind) {
        return schemas.get(kind);
    }
}
