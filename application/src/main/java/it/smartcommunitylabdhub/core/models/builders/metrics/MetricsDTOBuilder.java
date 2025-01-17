package it.smartcommunitylabdhub.core.models.builders.metrics;

import java.io.IOException;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.metrics.Metrics;
import it.smartcommunitylabdhub.core.models.entities.MetricsEntity;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MetricsDTOBuilder implements Converter<MetricsEntity, Metrics> {
    private static final TypeReference<Number[]> typeRef = new TypeReference<>() {};

    private static final ObjectMapper mapper = JacksonMapper.CBOR_OBJECT_MAPPER;

    public Metrics build(MetricsEntity entity) {
    	Number[] values = null;
        try {
            if ((entity.getValues() != null) && entity.getValues().length > 0) {
                values = mapper.readValue(entity.getValues(), typeRef);
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
        		.values(values)
        		.build();
    }
    
	@Override
	public Metrics convert(MetricsEntity source) {
		return build(source);
	}

}
