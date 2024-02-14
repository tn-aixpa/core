package it.smartcommunitylabdhub.core.components.infrastructure.factories.specs;

import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.schemas.Schema;
import it.smartcommunitylabdhub.commons.models.schemas.SchemaImpl;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.commons.utils.ErrorList;
import it.smartcommunitylabdhub.commons.utils.SchemaUtils;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.FactoryUtils;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpecRegistryImpl implements SpecRegistry {

    // A map to store spec types and their corresponding classes.
    private final Map<String, Class<? extends Spec>> specTypes = new HashMap<>();

    private final List<SpecFactory<? extends Spec>> specFactories;

    private final Map<String, Schema> specSchemas = new HashMap<>();

    public SpecRegistryImpl(List<SpecFactory<? extends Spec>> specFactories) {
        this.specFactories = specFactories;
    }

    @Override
    public void registerSpec(@NotNull String kind, @NotNull EntityName entity, @NotNull Class<? extends Spec> spec) {
        String key = extractKey(kind, entity);

        log.debug("register spec for {}:{} with class {} under key {}", entity, kind, spec.getName(), key);
        specTypes.put(key, spec);

        log.debug("generate schema for spec {}:{} ", entity, kind);

        SchemaImpl schema = SchemaImpl
            .builder()
            .key(key)
            .entity(entity)
            .kind(kind)
            .schema(SchemaUtils.schema(spec))
            .build();

        specSchemas.put(key, schema);
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
    public <S extends Spec> S createSpec(@NotNull String kind, @NotNull EntityName entity, Map<String, Object> data) {
        // Retrieve the class associated with the specified spec type.
        String key = extractKey(kind, entity);
        Class<? extends Spec> specClass = specTypes.get(key);

        if (specClass == null) {
            // Fallback spec None if no class specific is found, avoid crash.
            //specClass = (Class<? extends T>) specTypes.get("none_none");
            throw new CoreException(
                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                "Spec not found: tried to extract spec for <" + key + "> key",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        //pick a matching factory
        //TODO refactor with specType annotation when fixed
        SpecFactory<? extends Spec> specFactory = specFactories
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

    @Override
    public Schema getSchema(@NotNull String kind, @NotNull EntityName entity) {
        String key = extractKey(kind, entity);
        Schema schema = specSchemas.get(key);
        if (schema == null) {
            throw new IllegalArgumentException();
        }

        return schema;
    }

    @Override
    public Collection<Schema> listSchemas(EntityName entity) {
        return specSchemas
            .entrySet()
            .stream()
            .filter(e -> entity.name().equals(extractEntity(e.getKey())))
            .map(Entry::getValue)
            .collect(Collectors.toList());
    }

    private String extractKey(String kind, EntityName entity) {
        return entity.name().concat(SEPARATOR).concat(kind);
    }

    private String extractEntity(String key) {
        if (key == null || !key.contains(SEPARATOR)) {
            throw new IllegalArgumentException();
        }

        String[] p = key.split(SEPARATOR);
        return p[0];
    }

    private String extractKind(String key) {
        if (key == null || !key.contains(SEPARATOR)) {
            throw new IllegalArgumentException();
        }

        String[] p = key.split(SEPARATOR);
        return p[1];
    }

    private static final String SEPARATOR = "/";
}
