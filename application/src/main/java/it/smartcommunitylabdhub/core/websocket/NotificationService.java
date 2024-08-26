package it.smartcommunitylabdhub.core.websocket;

import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyFrontend(Run run) {
        log.info("----notification service, {}", run.getStatus().get("state").toString());
        try {
            messagingTemplate.convertAndSendToUser(run.getUser(), "/runs", run);
        } catch (MessagingException e) {
            log.error("Error sending message", e);
        }
    }
}
