package it.smartcommunitylabdhub.core.models.builders.run;

import it.smartcommunitylabdhub.core.components.fsm.enums.RunState;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.RunFieldAccessor;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import it.smartcommunitylabdhub.core.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RunEntityBuilder {


    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;


    /**
     * Build a Run from a RunDTO and store extra values as a cbor
     *
     * @param runDTO the run dto
     * @return Run
     */
    public RunEntity build(Run runDTO) {

        // Validate Spec
        specRegistry.createSpec(runDTO.getKind(), EntityName.RUN, Map.of());

        // Retrieve Field accessor
        RunFieldAccessor<?> runFieldAccessor =
                accessorRegistry.createAccessor(
                        runDTO.getKind(),
                        EntityName.RUN,
                        JacksonMapper.objectMapper.convertValue(
                                runDTO,
                                JacksonMapper.typeRef));


        // Create run Object
        RunEntity run = ConversionUtils.convert(runDTO, "run");
        // Retrieve base spec
        RunBaseSpec<?> spec = JacksonMapper.objectMapper
                .convertValue(runDTO.getSpec(), RunBaseSpec.class);

        // Merge Task and TaskId
        run.setTask(spec.getTask());
        run.setTaskId(spec.getTaskId());

        return EntityFactory.combine(
                run, runDTO,
                builder -> builder
                        .withIfElse(runFieldAccessor.getState().equals(State.NONE.name()),
                                (dto, condition) -> {
                                    if (condition) {
                                        dto.setStatus(ConversionUtils.convert(
                                                MapUtils.mergeMultipleMaps(
                                                        runFieldAccessor.getStatus(),
                                                        Map.of("state", State.CREATED.name())
                                                ), "cbor")
                                        );
                                        dto.setState(RunState.CREATED);
                                    } else {
                                        dto.setStatus(
                                                ConversionUtils.convert(
                                                        runFieldAccessor.getStatus(),
                                                        "cbor")
                                        );
                                        dto.setState(RunState.valueOf(runFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(r -> r.setMetadata(
                                ConversionUtils.convert(
                                        runDTO.getMetadata(),
                                        "metadata")))
                        .with(r -> r.setExtra(
                                ConversionUtils.convert(
                                        runDTO.getExtra(),
                                        "cbor")))
                        .with(r -> r.setSpec(
                                ConversionUtils.convert(
                                        spec.toMap(),
                                        "cbor"))));

    }

    /**
     * Update a Run if element is not passed it override causing empty field
     *
     * @param run    the Run
     * @param runDTO the run DTO
     * @return Run
     */
    public RunEntity update(RunEntity run, Run runDTO) {

        // Retrieve Field accessor
        RunFieldAccessor<?> runFieldAccessor =
                accessorRegistry.createAccessor(
                        runDTO.getKind(),
                        EntityName.RUN,
                        JacksonMapper.objectMapper.convertValue(
                                runDTO,
                                JacksonMapper.typeRef));

        return EntityFactory.combine(
                run, runDTO, builder -> builder
                        .withIfElse(runFieldAccessor.getState().equals(State.NONE.name()),
                                (dto, condition) -> {
                                    if (condition) {
                                        dto.setStatus(ConversionUtils.convert(
                                                MapUtils.mergeMultipleMaps(
                                                        runFieldAccessor.getStatus(),
                                                        Map.of("state", State.CREATED.name())
                                                ), "cbor")
                                        );
                                        dto.setState(RunState.CREATED);
                                    } else {
                                        dto.setStatus(
                                                ConversionUtils.convert(
                                                        runFieldAccessor.getStatus(),
                                                        "cbor")
                                        );
                                        dto.setState(RunState.valueOf(runFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(p -> p.setMetadata(
                                ConversionUtils.convert(runDTO
                                                .getMetadata(),
                                        "metadata"))));
    }
}
