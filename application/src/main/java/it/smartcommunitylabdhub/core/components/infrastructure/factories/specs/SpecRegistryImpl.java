package it.smartcommunitylabdhub.core.components.infrastructure.factories.specs;

import io.kubernetes.client.proto.V1.ConfigMap;
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
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Component
@Slf4j
@Validated
public class SpecRegistryImpl implements SpecRegistry, SpecValidator {

    private final Map<String, SpecType> specTypes = new HashMap<>();

    // A map to store spec types and their corresponding classes.
    private final Map<String, Class<? extends Spec>> specs = new HashMap<>();

    private final List<SpecFactory<? extends Spec>> factories;

    private final Map<String, Schema> schemas = new HashMap<>();

    private SmartValidator validator;

    public SpecRegistryImpl(List<SpecFactory<? extends Spec>> specFactories) {
        this.factories = specFactories;
    }

    @Autowired
    public void setValidator(SmartValidator validator) {
        this.validator = validator;
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

    @Override
    public void validateSpec(Spec spec) throws MethodArgumentNotValidException, IllegalArgumentException {
        // check with validator
        if (validator != null) {
            DataBinder binder = new DataBinder(spec);
            validator.validate(spec, binder.getBindingResult());
            if (binder.getBindingResult().hasErrors()) {
                try {
                    MethodParameter methodParameter = new MethodParameter(
                        this.getClass().getMethod("validateSpec", Spec.class),
                        0
                    );
                    throw new MethodArgumentNotValidException(methodParameter, binder.getBindingResult());
                } catch (NoSuchMethodException | SecurityException ex) {
                    StringBuilder sb = new StringBuilder();
                    binder
                        .getBindingResult()
                        .getFieldErrors()
                        .forEach(e -> {
                            sb.append(e.getField()).append(" ").append(e.getDefaultMessage()).append(", ");
                        });
                    String errorMsg = sb.toString();
                    throw new IllegalArgumentException(errorMsg);
                }
            }
        }
    }
}
