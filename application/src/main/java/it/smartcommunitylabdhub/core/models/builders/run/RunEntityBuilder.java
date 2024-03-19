package it.smartcommunitylabdhub.core.models.builders.run;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunMetadata;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import java.time.ZoneOffset;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RunEntityBuilder implements Converter<Run, RunEntity> {

    private final CBORConverter cborConverter;

    public RunEntityBuilder(CBORConverter cborConverter) {
        this.cborConverter = cborConverter;
    }

    /**
     * Build a Run from a RunDTO and store extra values as a cbor
     *
     * @param dto the run dto
     * @return Run
     */
    public RunEntity build(Run dto) {
        // Retrieve Field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        RunMetadata metadata = new RunMetadata();
        metadata.configure(dto.getMetadata());

        RunBaseSpec runSpec = new RunBaseSpec();
        runSpec.configure(dto.getSpec());

        return RunEntity
            .builder()
            .id(dto.getId())
            .kind(dto.getKind())
            .project(dto.getProject())
            .metadata(cborConverter.convert(dto.getMetadata()))
            .spec(cborConverter.convert(dto.getSpec()))
            .status(cborConverter.convert(dto.getStatus()))
            .extra(cborConverter.convert(dto.getExtra()))
            //extract task
            .task(runSpec.getTask())
            .state(
                // Store status if not present
                statusFieldAccessor.getState() == null ? State.CREATED : State.valueOf(statusFieldAccessor.getState())
            )
            // Metadata Extraction
            .created(
                metadata.getCreated() != null
                    ? Date.from(metadata.getCreated().atZone(ZoneOffset.UTC).toInstant())
                    : null
            )
            .updated(
                metadata.getUpdated() != null
                    ? Date.from(metadata.getUpdated().atZone(ZoneOffset.UTC).toInstant())
                    : null
            )
            .build();
    }

    @Override
    public RunEntity convert(Run source) {
        return build(source);
    }
}
