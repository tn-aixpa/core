package it.smartcommunitylabdhub.core.models.builders.run;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunMetadata;
import it.smartcommunitylabdhub.commons.models.entities.run.RunState;
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

        return RunEntity
            .builder()
            .id(dto.getId())
            .kind(dto.getKind())
            .project(dto.getProject())
            .metadata(cborConverter.convert(dto.getMetadata()))
            .spec(cborConverter.convert(spec))
            .status(cborConverter.convert(dto.getStatus()))
            .extra(cborConverter.convert(dto.getExtra()))
            //extract task
            .task(runSpec.getTask())
            .state(
                // Store status if not present
                statusFieldAccessor.getState() == null
                    ? RunState.CREATED
                    : RunState.valueOf(statusFieldAccessor.getState())
            )
            // Metadata Extraction
            .created(metadata.getCreated())
            .updated(metadata.getUpdated())
            .build();
    }

    @Override
    public RunEntity convert(Run source) {
        return build(source);
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
                                e.setState(RunState.CREATED);
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
