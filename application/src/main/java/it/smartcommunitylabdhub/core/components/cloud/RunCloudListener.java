package it.smartcommunitylabdhub.core.components.cloud;

import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.core.websocket.UserNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RunCloudListener {

    private UserNotificationService notificationService;

    @Autowired(required = false)
    public void setNotificationService(UserNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async
    @EventListener
    public void broadcast(CloudEntityEvent<Run> event) {
        Run run = event.getDto();

        if (run != null) {
            log.debug("receive event for {}: {}", run.getId(), event.getAction());

            if (notificationService != null) {
                //forward all events to users via notification
                //TODO support filtering/config
                notificationService.notifyOwner(run);
            }
        }
    }
}
