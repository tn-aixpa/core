package it.smartcommunitylabdhub.modules.mlrun.components.runnables.events.publishers;

import it.smartcommunitylabdhub.core.annotations.olders.RunPublisherComponent;
import it.smartcommunitylabdhub.core.components.kinds.factory.publishers.KindPublisher;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.modules.mlrun.components.runnables.events.messages.JobMessage;
import org.springframework.context.ApplicationEventPublisher;

@RunPublisherComponent(platform = "job", perform = "perform")
public class JobEventPublisher implements KindPublisher<Run, Void> {

    private final ApplicationEventPublisher applicationEventPublisher;

    public JobEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Void publish(Run runDTO) {
        // produce event with the runDTO object
        JobMessage jobMessage = new JobMessage(runDTO);
        applicationEventPublisher.publishEvent(jobMessage);
        return null;
    }

}
