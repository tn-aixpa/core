package it.smartcommunitylabdhub.core.models.builders.run;

import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RunDTOBuilder implements Converter<RunEntity, Run> {

    private final CBORConverter cborConverter;

    public RunDTOBuilder(CBORConverter cborConverter) {
        this.cborConverter = cborConverter;
    }

    public Run build(RunEntity entity) {
        //read metadata map as-is
        Map<String, Serializable> meta = cborConverter.reverseConvert(entity.getMetadata());

        // build metadata
        RunMetadata metadata = new RunMetadata();
        metadata.configure(meta);

        if (!StringUtils.hasText(metadata.getVersion())) {
            metadata.setVersion(entity.getId());
        }
        if (!StringUtils.hasText(metadata.getName())) {
            metadata.setName(entity.getName());
        }
        metadata.setProject(entity.getProject());
        metadata.setCreated(
            entity.getCreated() != null
                ? LocalDateTime.ofInstant(entity.getCreated().toInstant(), ZoneOffset.UTC)
                : null
        );
        metadata.setUpdated(
            entity.getUpdated() != null
                ? LocalDateTime.ofInstant(entity.getUpdated().toInstant(), ZoneOffset.UTC)
                : null
        );

        return Run
            .builder()
            .id(entity.getId())
            .kind(entity.getKind())
            .project(entity.getProject())
            .metadata(MapUtils.mergeMultipleMaps(meta, metadata.toMap()))
            .spec(cborConverter.reverseConvert(entity.getSpec()))
            .extra(cborConverter.reverseConvert(entity.getExtra()))
            .status(
                MapUtils.mergeMultipleMaps(
                    cborConverter.reverseConvert(entity.getStatus()),
                    Map.of("state", entity.getState().toString())
                )
            )
            .build();
    }

    @Override
    public Run convert(RunEntity source) {
        return build(source);
    }
}
