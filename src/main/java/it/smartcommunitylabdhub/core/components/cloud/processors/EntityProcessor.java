package it.smartcommunitylabdhub.core.components.cloud.processors;


import com.fasterxml.jackson.core.JsonProcessingException;
import it.smartcommunitylabdhub.core.components.cloud.events.EntitySavedEvent;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Slf4j
@Component
public class EntityProcessor {
    @Bean
    public Function<EntitySavedEvent<?>, String> serializeEntity() {
        return (value) -> {
            log.info("Received {}", value.getEntity());
            try {
                String serializedEntity = serializeToJson(value.getEntity());
                log.info("Sending {}", serializedEntity);
                return serializedEntity;

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private String serializeToJson(Object entity) throws JsonProcessingException {
        // Implement your serialization logic here
        // For example, using Jackson ObjectMapper
        return JacksonMapper.CBOR_OBJECT_MAPPER.writeValueAsString(entity);
    }
}
