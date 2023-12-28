package it.smartcommunitylabdhub.core.components.cloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.smartcommunitylabdhub.core.components.cloud.events.EntitySavedEvent;
import it.smartcommunitylabdhub.core.models.base.interfaces.BaseEntity;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.core.utils.jackson.mixins.CborMixin;
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
    public <T extends BaseEntity> void handleEntitySavedEvent(EntitySavedEvent<T> event) {
        // Broadcast event on rabbit amqp
        try {
            JacksonMapper.OBJECT_MAPPER.addMixIn(event.getEntity().getClass(), CborMixin.class);
            String serializedEntity = JacksonMapper.OBJECT_MAPPER
                    .writeValueAsString(event.getEntity());
            streamBridge.send("entity-topic", serializedEntity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


//        Other services
//        streamBridge.send("entityOutput-kafka-out-0", message);
    }

}
