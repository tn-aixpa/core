package it.smartcommunitylabdhub.core.components.cloud;

import it.smartcommunitylabdhub.core.components.cloud.events.EntitySavedEvent;
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

        // Broadcast event on rabbit amqp
        streamBridge.send("entity-topic", event);


//        Other services
//        streamBridge.send("entityOutput-kafka-out-0", message);
    }

}
