package it.smartcommunitylabdhub.core.models.builders.run;

import it.smartcommunitylabdhub.core.components.fsm.enums.RunState;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.RunFieldAccessor;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

        // Retrieve base spec
        RunBaseSpec<?> spec = JacksonMapper.objectMapper
                .convertValue(runDTO.getSpec(), RunBaseSpec.class);


        return EntityFactory.combine(
                ConversionUtils.convert(runDTO, "run"), runDTO, builder -> builder
                        // check id
                        .withIfElse(runDTO.getId() != null &&
                                        runDTO.getMetadata().getVersion() != null,
                                (r) -> {
                                    if (runDTO.getId()
                                            .equals(runDTO.getMetadata().getVersion())) {
                                        r.setId(runDTO.getMetadata().getVersion());
                                    } else {
                                        throw new CoreException(
                                                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                                                "Trying to store item with which has different signature <id != version>",
                                                HttpStatus.INTERNAL_SERVER_ERROR
                                        );
                                    }
                                },
                                (r) -> {
                                    if (runDTO.getId() == null &&
                                            runDTO.getMetadata().getVersion() != null) {
                                        r.setId(runDTO.getMetadata().getVersion());
                                    } else {
                                        r.setId(runDTO.getId());
                                    }
                                })
                        .with(r -> r.setTask(spec.getTask()))
                        .with(r -> r.setTaskId(spec.getTaskId()))
                        .withIfElse(runFieldAccessor.getState().equals(State.NONE.name()),
                                (r, condition) -> {
                                    if (condition) {
                                        r.setState(RunState.CREATED);
                                    } else {
                                        r.setState(RunState.valueOf(runFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(r -> r.setMetadata(ConversionUtils.convert(
                                runDTO.getMetadata(), "metadata")))
                        .with(r -> r.setExtra(ConversionUtils.convert(
                                runDTO.getExtra(), "cbor")))
                        .with(r -> r.setSpec(ConversionUtils.convert(
                                spec.toMap(), "cbor")))
                        .with(r -> r.setStatus(ConversionUtils.convert(
                                runDTO.getStatus(), "cbor")))
                        .withIf(runDTO.getMetadata().getCreated() != null, (r) ->
                                r.setCreated(runDTO.getMetadata().getCreated()))
                        .withIf(runDTO.getMetadata().getUpdated() != null, (r) ->
                                r.setUpdated(runDTO.getMetadata().getUpdated())
                        )
        );

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
                                (r, condition) -> {
                                    if (condition) {
                                        r.setState(RunState.CREATED);
                                    } else {
                                        r.setState(RunState.valueOf(runFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(r -> r.setStatus(ConversionUtils.convert(
                                runDTO.getStatus(), "cbor")))
                        .with(p -> p.setMetadata(ConversionUtils.convert(
                                runDTO.getMetadata(), "metadata"))));
    }
}
