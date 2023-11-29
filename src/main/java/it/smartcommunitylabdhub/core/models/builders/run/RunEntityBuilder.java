package it.smartcommunitylabdhub.core.models.builders.run;

import it.smartcommunitylabdhub.core.components.fsm.enums.RunState;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RunEntityBuilder {


    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    /**
     * Build a Run from a RunDTO and store extra values as a cbor
     *
     * @param runDTO the run dto
     * @return Run
     */
    public RunEntity build(Run runDTO) {

        specRegistry.createSpec(runDTO.getKind(), EntityName.RUN, Map.of());

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

        return EntityFactory.combine(
                run, runDTO, builder -> builder
                        .with(r -> r.setState(runDTO.getState() == null
                                ? RunState.CREATED
                                : RunState.valueOf(
                                runDTO.getState())))
                        .with(p -> p.setMetadata(
                                ConversionUtils.convert(runDTO
                                                .getMetadata(),
                                        "metadata"))));
    }
}
