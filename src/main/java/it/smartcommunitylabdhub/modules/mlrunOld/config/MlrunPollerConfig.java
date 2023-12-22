package it.smartcommunitylabdhub.modules.mlrunOld.config;

import it.smartcommunitylabdhub.core.components.pollers.PollingService;
import it.smartcommunitylabdhub.core.components.workflows.factory.Workflow;
import it.smartcommunitylabdhub.modules.mlrunOld.components.pollers.functions.FunctionWorkflowBuilder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MlrunPollerConfig {

    @Autowired
    FunctionWorkflowBuilder functionWorkflowBuilder;
    @Autowired
    private PollingService pollingService;

    @PostConstruct
    public void initialize() {

        // Create and configure Function Workflow
        List<Workflow> coreMlrunSyncWorkflow = new ArrayList<>();
        coreMlrunSyncWorkflow.add(functionWorkflowBuilder.build());

        // Create a new poller and start it.
        pollingService.createPoller("DHCore-Mlrun-Sync", coreMlrunSyncWorkflow, 5, true, true);
        pollingService.startOne("DHCore-Mlrun-Sync");
    }

}
