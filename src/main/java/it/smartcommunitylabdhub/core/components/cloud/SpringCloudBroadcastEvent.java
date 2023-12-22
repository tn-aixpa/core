package it.smartcommunitylabdhub.core.components.cloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.smartcommunitylabdhub.core.components.cloud.events.EntitySavedEvent;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringCloudBroadcastEvent {

    @Autowired
    private StreamBridge streamBridge;

    @EventListener
    public void handleEntitySavedEvent(EntitySavedEvent<?> event) {

        try {
            // Handle the Spring Data event
            log.info("Handling Spring Data entity saved event: " + event.getEntity());

            // Broadcast event on rabbit amqp
            streamBridge.send("entityEvent-rabbit-out-0", serializeToJson(event.getEntity()));
        } catch (JsonProcessingException e) {
            log.error("Error serializing entity to JSON", e);
        }

//        Other services
//        streamBridge.send("entityOutput-kafka-out-0", message);
    }

    private String serializeToJson(Object entity) throws JsonProcessingException {
        // Implement your serialization logic here
        // For example, using Jackson ObjectMapper
        return JacksonMapper.OBJECT_MAPPER.writeValueAsString(entity);
    }

}
