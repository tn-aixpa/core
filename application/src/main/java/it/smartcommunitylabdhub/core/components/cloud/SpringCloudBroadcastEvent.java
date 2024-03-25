package it.smartcommunitylabdhub.core.components.cloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.core.models.events.EntityAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "event-queue.enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class SpringCloudBroadcastEvent {

    private static final ObjectMapper mapper = JacksonMapper.CBOR_OBJECT_MAPPER;

    private final RabbitTemplate rabbitTemplate;

    public SpringCloudBroadcastEvent(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Async
    @EventListener
    public void handleEntitySavedEvent(CloudEntityEvent<?> event) {
        // Broadcast event on rabbit amqp
        try {
            if (event.getAction() != EntityAction.DELETE) {
                rabbitTemplate.convertAndSend("entityTopic", "entityRoutingKey", mapper.writeValueAsString(event));
            }
        } catch (JsonProcessingException e) {
            log.error("Error serializing cloud event for rabbit", e.getMessage());
        }
    }
}
