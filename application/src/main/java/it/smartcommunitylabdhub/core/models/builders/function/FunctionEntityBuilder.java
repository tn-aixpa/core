package it.smartcommunitylabdhub.core.models.builders.function;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionMetadata;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import java.io.Serializable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FunctionEntityBuilder implements Converter<Function, FunctionEntity> {

    @Autowired
    CBORConverter cborConverter;

    @Autowired
    SpecRegistry specRegistry;

    /**
     * Build a function from a functionDTO and store extra values as f cbor
     * <p>
     *
     * @param dto the functionDTO that need to be stored
     * @return Function
     */
    public FunctionEntity build(Function dto) {
        // Parse and export Spec
        Map<String, Serializable> spec = specRegistry
            .createSpec(dto.getKind(), EntityName.FUNCTION, dto.getSpec())
            .toMap();

        // Retrieve field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        FunctionMetadata metadata = new FunctionMetadata();
        metadata.configure(dto.getMetadata());

        return EntityFactory.combine(
            FunctionEntity.builder().build(),
            builder ->
                builder
                    // check id
                    .withIf(dto.getId() != null, e -> e.setId(dto.getId()))
                    .with(e -> e.setName(dto.getName()))
                    .with(e -> e.setKind(dto.getKind()))
                    .with(e -> e.setProject(dto.getProject()))
                    .with(e -> e.setMetadata(cborConverter.convert(dto.getMetadata())))
                    .with(e -> e.setSpec(cborConverter.convert(spec)))
                    .with(e -> e.setStatus(cborConverter.convert(dto.getStatus())))
                    .with(e -> e.setExtra(cborConverter.convert(dto.getExtra())))
                    // Store status if not present
                    .withIfElse(
                        (statusFieldAccessor.getState() == null),
                        (f, condition) -> {
                            if (condition) {
                                f.setState(State.CREATED);
                            } else {
                                f.setState(State.valueOf(statusFieldAccessor.getState()));
                            }
                        }
                    )
                    // Metadata Extraction
                    .withIfElse(
                        metadata.getEmbedded() == null,
                        (a, condition) -> {
                            if (condition) {
                                a.setEmbedded(false);
                            } else {
                                a.setEmbedded(metadata.getEmbedded());
                            }
                        }
                    )
                    .withIf(metadata.getCreated() != null, e -> e.setCreated(metadata.getCreated()))
                    .withIf(metadata.getUpdated() != null, e -> e.setUpdated(metadata.getUpdated()))
        );
    }

    @Override
    public FunctionEntity convert(Function source) {
        return build(source);
    }

    /**
     * Update a function if element is not passed it override causing empty field
     *
     * @param function the function to update
     * @return Function
     */
    public FunctionEntity update(FunctionEntity function, Function functionDTO) {
        FunctionEntity newFunction = build(functionDTO);
        return doUpdate(function, newFunction);
    }

    private FunctionEntity doUpdate(FunctionEntity function, FunctionEntity newFunction) {
        return EntityFactory.combine(
            function,
            builder ->
                builder
                    .withIfElse(
                        newFunction.getState().name().equals(State.NONE.name()),
                        (f, condition) -> {
                            if (condition) {
                                f.setState(State.CREATED);
                            } else {
                                f.setState(newFunction.getState());
                            }
                        }
                    )
                    .with(e -> e.setMetadata(newFunction.getMetadata()))
                    .with(e -> e.setExtra(newFunction.getExtra()))
                    .with(e -> e.setStatus(newFunction.getStatus()))
                    // Metadata Extraction
                    .withIfElse(
                        newFunction.getEmbedded() == null,
                        (f, condition) -> {
                            if (condition) {
                                f.setEmbedded(false);
                            } else {
                                f.setEmbedded(newFunction.getEmbedded());
                            }
                        }
                    )
        );
    }
}
