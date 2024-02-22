package it.smartcommunitylabdhub.core.models.builders.run;

import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import java.io.Serializable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RunDTOBuilder implements Converter<RunEntity, Run> {

    @Autowired
    CBORConverter cborConverter;

    public Run build(RunEntity entity) {
        return EntityFactory.create(
            Run::new,
            builder ->
                builder
                    .with(dto -> dto.setId(entity.getId()))
                    .with(dto -> dto.setKind(entity.getKind()))
                    .with(dto -> dto.setProject(entity.getProject()))
                    .with(dto -> {
                        //read metadata as-is
                        Map<String, Serializable> meta = cborConverter.reverseConvert(entity.getMetadata());

                        // Set Metadata for run
                        RunMetadata metadata = new RunMetadata();
                        metadata.configure(meta);

                        metadata.setVersion(entity.getId());
                        metadata.setProject(entity.getProject());
                        metadata.setCreated(entity.getCreated());
                        metadata.setUpdated(entity.getUpdated());

                        //merge into map with override
                        dto.setMetadata(MapUtils.mergeMultipleMaps(meta, metadata.toMap()));
                    })
                    .with(dto -> dto.setSpec(cborConverter.reverseConvert(entity.getSpec())))
                    .with(dto -> dto.setExtra(cborConverter.reverseConvert(entity.getExtra())))
                    .with(dto ->
                        dto.setStatus(
                            MapUtils.mergeMultipleMaps(
                                cborConverter.reverseConvert(entity.getStatus()),
                                Map.of("state", entity.getState().toString())
                            )
                        )
                    )
        );
    }

    @Override
    public Run convert(RunEntity source) {
        return build(source);
    }
}
