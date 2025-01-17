package it.smartcommunitylabdhub.core.models.builders.metrics;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.metrics.Metrics;
import it.smartcommunitylabdhub.core.models.entities.MetricsEntity;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MetricsEntityBuilder implements Converter<Metrics, MetricsEntity> {
	
	private final ObjectMapper mapper = JacksonMapper.CBOR_OBJECT_MAPPER;
	
	public MetricsEntity build(Metrics dto) {
        byte[] value = null;
        try {
            if (dto.getValues() != null) {
                value = mapper.writeValueAsBytes(dto.getValues());
            }
        } catch (JsonProcessingException e) {
            log.error("MetricsEntity build error: {}", e.getMessage());
        }
        
        return MetricsEntity
        		.builder()
        		.id(dto.getId())
        		.entityId(dto.getEntityId())
        		.entityName(dto.getEntityName())
        		.name(dto.getName())
        		.values(value)
        		.build();
	}
	
	@Override
	public MetricsEntity convert(Metrics source) {
		return build(source);
	}

}
