package it.smartcommunitylabdhub.core.models.builders.run;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunMetadata;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.ErrorList;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RunEntityBuilder implements Converter<Run, RunEntity> {

    @Autowired
    CBORConverter cborConverter;

    @Autowired
    SpecRegistry specRegistry;

    /**
     * Build a Run from a RunDTO and store extra values as a cbor
     *
     * @param dto the run dto
     * @return Run
     */
    public RunEntity build(Run dto) {
        // Parse and export Spec
        Map<String, Serializable> spec = specRegistry.createSpec(dto.getKind(), EntityName.RUN, dto.getSpec()).toMap();

        // Retrieve Field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        RunMetadata metadata = new RunMetadata();
        metadata.configure(dto.getMetadata());
        RunBaseSpec runSpec = new RunBaseSpec();
        runSpec.configure(spec);

        return EntityFactory.combine(
            RunEntity.builder().build(),
            builder ->
                builder
                    // check id
                    .withIf(dto.getId() != null, e -> e.setId(dto.getId()))
                    .with(e -> e.setProject(dto.getProject()))
                    .with(e -> e.setKind(dto.getKind()))
                    .with(e ->
                        e.setTask(
                            Optional
                                .ofNullable(StringUtils.hasText(runSpec.getTask()) ? runSpec.getTask() : null)
                                .orElseThrow(() ->
                                    new CoreException(
                                        ErrorList.TASK_NOT_FOUND.getValue(),
                                        ErrorList.TASK_NOT_FOUND.getReason(),
                                        HttpStatus.INTERNAL_SERVER_ERROR
                                    )
                                )
                        )
                    )
                    .with(e -> e.setTaskId(runSpec.getTaskId()))
                    .withIfElse(
                        (statusFieldAccessor.getState() == null),
                        (e, condition) -> {
                            if (condition) {
                                e.setState(State.CREATED);
                            } else {
                                e.setState(State.valueOf(statusFieldAccessor.getState()));
                            }
                        }
                    )
                    .with(e -> e.setMetadata(cborConverter.convert(dto.getMetadata())))
                    .with(e -> e.setSpec(cborConverter.convert(spec)))
                    .with(e -> e.setStatus(cborConverter.convert(dto.getStatus())))
                    .with(e -> e.setExtra(cborConverter.convert(dto.getExtra())))
                    .withIf(metadata.getCreated() != null, e -> e.setCreated(metadata.getCreated()))
                    .withIf(metadata.getUpdated() != null, e -> e.setUpdated(metadata.getUpdated()))
        );
    }

    @Override
    public RunEntity convert(Run source) {
        return build(source);
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
            builder ->
                builder
                    .withIfElse(
                        newRun.getState().name().equals(State.NONE.name()),
                        (e, condition) -> {
                            if (condition) {
                                e.setState(State.CREATED);
                            } else {
                                e.setState(newRun.getState());
                            }
                        }
                    )
                    .with(e -> e.setStatus(newRun.getStatus()))
                    .with(e -> e.setMetadata(newRun.getMetadata()))
        );
    }
}
