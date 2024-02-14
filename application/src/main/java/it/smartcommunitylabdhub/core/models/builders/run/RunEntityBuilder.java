package it.smartcommunitylabdhub.core.models.builders.run;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunState;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.commons.utils.ErrorList;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RunEntityBuilder {

    @Autowired
    SpecRegistry specRegistry;

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
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(runDTO.getStatus());

        // Retrieve base spec
        RunBaseSpec spec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(runDTO.getSpec(), RunBaseSpec.class);

        return EntityFactory.combine(
            RunEntity.builder().build(),
            runDTO,
            builder ->
                builder
                    // check id
                    .withIf(runDTO.getId() != null, r -> r.setId(runDTO.getId()))
                    .with(r -> r.setProject(runDTO.getProject()))
                    .with(r -> r.setKind(runDTO.getKind()))
                    .with(r ->
                        r.setTask(
                            Optional
                                .ofNullable(StringUtils.hasText(spec.getTask()) ? spec.getTask() : null)
                                .orElseThrow(() ->
                                    new CoreException(
                                        ErrorList.TASK_NOT_FOUND.getValue(),
                                        ErrorList.TASK_NOT_FOUND.getReason(),
                                        HttpStatus.INTERNAL_SERVER_ERROR
                                    )
                                )
                        )
                    )
                    .with(r -> r.setTaskId(spec.getTaskId()))
                    .withIfElse(
                        statusFieldAccessor.getState().equals(State.NONE.name()),
                        (r, condition) -> {
                            if (condition) {
                                r.setState(RunState.CREATED);
                            } else {
                                r.setState(RunState.valueOf(statusFieldAccessor.getState()));
                            }
                        }
                    )
                    .with(r -> r.setMetadata(ConversionUtils.convert(runDTO.getMetadata(), "metadata")))
                    .with(r -> r.setExtra(ConversionUtils.convert(runDTO.getExtra(), "cbor")))
                    .with(r -> r.setSpec(ConversionUtils.convert(spec.toMap(), "cbor")))
                    .with(r -> r.setStatus(ConversionUtils.convert(runDTO.getStatus(), "cbor")))
                    .withIf(
                        runDTO.getMetadata().getCreated() != null,
                        r -> r.setCreated(runDTO.getMetadata().getCreated())
                    )
                    .withIf(
                        runDTO.getMetadata().getUpdated() != null,
                        r -> r.setUpdated(runDTO.getMetadata().getUpdated())
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
        RunEntity newRun = build(runDTO);
        return doUpdate(run, newRun);
    }

    /**
     * Updates the given RunEntity with the properties of the newRun entity.
     *
     * @param run    the original RunEntity to be updated
     * @param newRun the new RunEntity containing the updated properties
     * @return the updated RunEntity after the merge operation
     */
    public RunEntity doUpdate(RunEntity run, RunEntity newRun) {
        return EntityFactory.combine(
            run,
            newRun,
            builder ->
                builder
                    .withIfElse(
                        newRun.getState().name().equals(State.NONE.name()),
                        (r, condition) -> {
                            if (condition) {
                                r.setState(RunState.CREATED);
                            } else {
                                r.setState(newRun.getState());
                            }
                        }
                    )
                    .with(r -> r.setStatus(newRun.getStatus()))
                    .with(p -> p.setMetadata(newRun.getMetadata()))
        );
    }
}
