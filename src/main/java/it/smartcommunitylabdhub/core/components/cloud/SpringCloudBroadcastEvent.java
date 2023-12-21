package it.smartcommunitylabdhub.core.components.cloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.smartcommunitylabdhub.core.components.cloud.events.EntitySavedEvent;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SpringCloudBroadcastEvent {

    @Autowired
    private StreamBridge streamBridge;

    @EventListener
    public void handleEntitySavedEvent(EntitySavedEvent<?> event) throws JsonProcessingException {
        // Handle the Spring Data event
        System.out.println("Handling Spring Data entity saved event: " + event.getEntity());

        // TODO: Broadcast to Spring Cloud Stream channels if needed
        streamBridge.send("entityOutput-rabbit-out-0", serializeToJson(event.getEntity()));
//
//        Other services
//        streamBridge.send("entityOutput-kafka-out-0", message);
    }


    private String serializeToJson(Object entity) throws JsonProcessingException {

        return JacksonMapper.OBJECT_MAPPER.writeValueAsString(entity);
    }
}
