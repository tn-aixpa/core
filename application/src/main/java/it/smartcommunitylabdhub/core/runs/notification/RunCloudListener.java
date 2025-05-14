package it.smartcommunitylabdhub.core.runs.notification;

import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.run.Run;
import it.smartcommunitylabdhub.core.components.cloud.CloudEntityEvent;
import it.smartcommunitylabdhub.core.websocket.UserNotification;
import it.smartcommunitylabdhub.core.websocket.UserNotificationEntityEvent;
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
    public void notify(UserNotificationEntityEvent<Run> event) {
        Run dto = event.getDto();
        String user = event.getUser();

        if (event.getUser() == null) {
            log.warn("notification has no user");
            return;
        }

        if (notificationService != null) {
            log.debug("receive notify for {}: {}", user, dto.getId());

            //unpack and notify
            UserNotification<Run> notification = UserNotification
                .<Run>builder()
                .action(event.getAction())
                .entity(EntityName.RUN)
                .user(user)
                .dto(dto)
                .build();

            notificationService.notifyUser(notification);
        }
    }

    @Async
    @EventListener
    public void broadcast(CloudEntityEvent<Run> event) {
        Run run = event.getDto();

        if (run != null) {
            log.debug("receive event for {}: {}", run.getId(), event.getAction());

            if (notificationService != null && event.getDto() != null) {
                //unpack and notify
                UserNotification<Run> notification = UserNotification
                    .<Run>builder()
                    .action(event.getAction())
                    .entity(EntityName.RUN)
                    .dto(event.getDto())
                    .build();

                //forward all events to users via notification
                notificationService.broadcast(notification);
            }
        }
    }
}
