package it.smartcommunitylabdhub.metrics;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.metrics.Metrics;
import it.smartcommunitylabdhub.commons.models.metrics.NumberOrNumberArray;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MetricsDTOBuilder implements Converter<MetricsEntity, Metrics> {

    private static final TypeReference<NumberOrNumberArray> typeRef = new TypeReference<>() {};

    private static final ObjectMapper mapper = JacksonMapper.CBOR_OBJECT_MAPPER;

    public Metrics build(MetricsEntity entity) {
        NumberOrNumberArray data = null;
        try {
            if ((entity.getData() != null) && entity.getData().length > 0) {
                data = mapper.readValue(entity.getData(), typeRef);
            }
        } catch (IOException e) {
            log.error("Metrics build error: {}", e.getMessage());
        }

        return Metrics
            .builder()
            .id(entity.getId())
            .entityId(entity.getEntityId())
            .entityName(entity.getEntityName())
            .name(entity.getName())
            .data(data)
            .build();
    }

    @Override
    public Metrics convert(@NonNull MetricsEntity source) {
        return build(source);
    }
}
