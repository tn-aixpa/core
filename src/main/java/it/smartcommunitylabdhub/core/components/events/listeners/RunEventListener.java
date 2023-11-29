package it.smartcommunitylabdhub.core.components.events.listeners;

import it.smartcommunitylabdhub.core.components.events.messages.RunMessage;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.components.kinds.factory.workflows.KindWorkflowFactory;
import it.smartcommunitylabdhub.core.components.pollers.PollingService;
import it.smartcommunitylabdhub.core.components.workflows.factory.Workflow;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunAccessor;
import it.smartcommunitylabdhub.core.models.accessors.utils.RunUtils;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RunEventListener {

    @Autowired
    KindWorkflowFactory kindWorkflowFactory;
    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Autowired
    private PollingService pollingService;

    @EventListener
    @Async
    public void handle(RunMessage message) {

        List<Workflow> workflows = new ArrayList<>();

        RunBaseSpec<?> runBaseSpec = specRegistry.createSpec(
                message.getRunDTO().getKind(),
                EntityName.RUN,
                message.getRunDTO().getSpec()
        );

        RunAccessor runAccessor = RunUtils.parseRun(runBaseSpec.getTask());

        // This kindWorkflowFactory allow specific workflow generation based on task
        // field type
        workflows.add((Workflow) kindWorkflowFactory
                .getWorkflow(runAccessor.getRuntime(), runAccessor.getTask())
                .build(message.getRunDTO()));

        // Create new run poller
        pollingService.createPoller("run:" + message.getRunDTO().getId(),
                workflows, 2, true, false);

        // Start poller
        pollingService.startOne("run:" + message.getRunDTO().getId());
    }
}
