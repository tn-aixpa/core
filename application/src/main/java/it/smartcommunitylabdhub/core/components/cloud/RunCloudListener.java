package it.smartcommunitylabdhub.core.components.cloud;

import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.core.websocket.UserNotification;
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

            if (notificationService != null && event.getDto() != null) {
                //forward all events to users via notification
                //TODO support filtering/config
                Run dto = event.getDto();
                UserNotification<Run> notification = UserNotification
                    .<Run>builder()
                    .action(event.getAction())
                    .entity(EntityName.RUN)
                    .user(dto.getUser())
                    .dto(dto)
                    .build();

                notificationService.notifyOwner(notification);
            }
        }
    }
}
