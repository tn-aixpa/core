package it.smartcommunitylabdhub.core.models.builders.log;

import it.smartcommunitylabdhub.commons.models.entities.log.Log;
import it.smartcommunitylabdhub.commons.models.entities.log.LogMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.log.LogEntity;
import java.io.Serializable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LogDTOBuilder implements Converter<LogEntity, Log> {

    @Autowired
    CBORConverter cborConverter;

    public Log build(LogEntity entity) {
        return EntityFactory.create(
            Log::new,
            entity,
            builder ->
                builder
                    .with(dto -> dto.setId(entity.getId()))
                    .with(dto -> {
                        //read metadata as-is
                        Map<String, Serializable> meta = cborConverter.reverseConvert(entity.getMetadata());

                        // Set Metadata for log
                        LogMetadata metadata = new LogMetadata();
                        metadata.configure(meta);

                        metadata.setRun(entity.getRun());
                        metadata.setProject(entity.getProject());
                        metadata.setCreated(entity.getCreated());
                        metadata.setUpdated(entity.getUpdated());

                        //merge into map with override
                        dto.setMetadata(MapUtils.mergeMultipleMaps(meta, metadata.toMap()));
                    })
                    .with(dto -> dto.setBody(cborConverter.reverseConvert(entity.getBody())))
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
    public Log convert(LogEntity source) {
        return build(source);
    }
}
