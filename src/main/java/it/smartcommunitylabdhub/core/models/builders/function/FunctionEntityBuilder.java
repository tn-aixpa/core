package it.smartcommunitylabdhub.core.models.builders.function;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.FunctionFieldAccessor;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.function.Function;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class FunctionEntityBuilder {

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;

    /**
     * Build a function from a functionDTO and store extra values as a cbor
     * <p>
     *
     * @param functionDTO the functionDTO that need to be stored
     * @return Function
     */
    public FunctionEntity build(Function functionDTO) {

        // Validate spec
        specRegistry.createSpec(functionDTO.getKind(), EntityName.FUNCTION, Map.of());

        // Retrieve field accessor
        FunctionFieldAccessor<?> functionFieldAccessor =
                accessorRegistry.createAccessor(
                        functionDTO.getKind(),
                        EntityName.FUNCTION,
                        JacksonMapper.objectMapper.convertValue(functionDTO,
                                JacksonMapper.typeRef));

        // Retrieve Spec
        FunctionBaseSpec<?> spec = JacksonMapper.objectMapper
                .convertValue(functionDTO.getSpec(), FunctionBaseSpec.class);

        return EntityFactory.combine(
                ConversionUtils.convert(functionDTO, "function"), functionDTO, builder -> builder
                        .with(p -> p.setMetadata(ConversionUtils.convert(
                                functionDTO.getMetadata(), "metadata")))
                        .with(a -> a.setExtra(ConversionUtils.convert(
                                functionDTO.getExtra(), "cbor")))
                        .with(a -> a.setSpec(ConversionUtils.convert(
                                spec.toMap(), "cbor")))

                        // Store status if not present
                        .withIfElse(functionFieldAccessor.getState().equals(State.NONE.name()),
                                (a, condition) -> {
                                    if (condition) {
                                        a.setState(State.CREATED);
                                    } else {
                                        a.setState(State.valueOf(functionFieldAccessor.getState()));
                                    }
                                }
                        )

                        // Metadata Extraction
                        .withIfElse(functionDTO.getMetadata().getEmbedded() == null,
                                (a, condition) -> {
                                    if (condition) {
                                        a.setEmbedded(false);
                                    } else {
                                        a.setEmbedded(functionDTO.getMetadata().getEmbedded());
                                    }
                                }
                        )
                        .withIf(functionDTO.getMetadata().getCreated() != null, (a) ->
                                a.setCreated(functionDTO.getMetadata().getCreated()))
                        .withIf(functionDTO.getMetadata().getUpdated() != null, (a) ->
                                a.setUpdated(functionDTO.getMetadata().getUpdated()))
        );
    }

    /**
     * Update a function if element is not passed it override causing empty field
     *
     * @param function the function to update
     * @return Function
     */
    public FunctionEntity update(FunctionEntity function, Function functionDTO) {

        // Validate spec
        specRegistry.createSpec(functionDTO.getKind(), EntityName.FUNCTION, Map.of());

        // Retrieve field accessor
        FunctionFieldAccessor<?> functionFieldAccessor =
                accessorRegistry.createAccessor(
                        functionDTO.getKind(),
                        EntityName.FUNCTION,
                        JacksonMapper.objectMapper.convertValue(functionDTO,
                                JacksonMapper.typeRef));

        return EntityFactory.combine(
                function, functionDTO, builder -> builder
                        .withIfElse(functionFieldAccessor.getState().equals(State.NONE.name()),
                                (a, condition) -> {
                                    if (condition) {
                                        a.setState(State.CREATED);
                                    } else {
                                        a.setState(State.valueOf(functionFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(a -> a.setMetadata(ConversionUtils.convert(functionDTO
                                .getMetadata(), "metadata")))
                        .with(a -> a.setExtra(ConversionUtils.convert(functionDTO
                                .getExtra(), "cbor")))

                        // Metadata Extraction
                        .withIfElse(functionDTO.getMetadata().getEmbedded() == null,
                                (a, condition) -> {
                                    if (condition) {
                                        a.setEmbedded(false);
                                    } else {
                                        a.setEmbedded(functionDTO.getMetadata().getEmbedded());
                                    }
                                }
                        )
        );
    }
}
