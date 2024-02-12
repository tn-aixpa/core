package it.smartcommunitylabdhub.core.models.builders.run;

import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.metadata.RunMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RunDTOBuilder {

    @Autowired
    MetadataConverter<RunMetadata> metadataConverter;

    public Run build(RunEntity run) {
        return EntityFactory.create(
            Run::new,
            run,
            builder ->
                builder
                    .with(dto -> dto.setId(run.getId()))
                    .with(dto -> dto.setKind(run.getKind()))
                    .with(dto -> dto.setProject(run.getProject()))
                    .with(dto -> {
                        // Set Metadata for run
                        RunMetadata runMetadata = Optional
                            .ofNullable(metadataConverter.reverseByClass(run.getMetadata(), RunMetadata.class))
                            .orElseGet(RunMetadata::new);
                        runMetadata.setVersion(run.getId());
                        runMetadata.setProject(run.getProject());
                        runMetadata.setCreated(run.getCreated());
                        runMetadata.setUpdated(run.getUpdated());
                        dto.setMetadata(runMetadata);
                    })
                    .with(dto -> dto.setSpec(ConversionUtils.reverse(run.getSpec(), "cbor")))
                    .with(dto -> dto.setExtra(ConversionUtils.reverse(run.getExtra(), "cbor")))
                    .with(dto ->
                        dto.setStatus(
                            MapUtils.mergeMultipleMaps(
                                ConversionUtils.reverse(run.getStatus(), "cbor"),
                                Map.of("state", run.getState())
                            )
                        )
                    )
        );
    }
}
