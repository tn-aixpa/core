package it.smartcommunitylabdhub.core.components.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import jakarta.persistence.PostPersist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EntityEventListener {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @PostPersist
    public void handlePostPersist(Object entity) {
//        try {
//            // Send the entity to RabbitMQ
//            amqpTemplate.convertAndSend("dhcore_queue", serializeToJson(entity));
//        } catch (JsonProcessingException e) {
//            log.error("Error processing entity for RabbitMQ", e);
//        }
    }

    private String serializeToJson(Object entity) throws JsonProcessingException {

        return JacksonMapper.OBJECT_MAPPER.writeValueAsString(entity);
    }
}
