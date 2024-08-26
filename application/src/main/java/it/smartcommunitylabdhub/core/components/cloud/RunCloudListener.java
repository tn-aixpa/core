package it.smartcommunitylabdhub.core.components.cloud;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.core.websocket.NotificationService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RunCloudListener {
    private final NotificationService notificationService;

    public RunCloudListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async
    @EventListener
    public void broadcast(CloudEntityEvent<Run> event) {
        Run run = event.getDto();
        log.info("----catched run event, {}, {}", run.getId(), run.getStatus().get("state").toString());
        notificationService.notifyFrontend(run);
    }
}
