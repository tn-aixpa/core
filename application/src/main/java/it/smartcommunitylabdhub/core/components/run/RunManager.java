package it.smartcommunitylabdhub.core.components.run;

import it.smartcommunitylabdhub.commons.events.RunChangedEvent;
import it.smartcommunitylabdhub.commons.events.RunMonitorObject;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.fsm.Fsm;
import it.smartcommunitylabdhub.fsm.enums.RunEvent;
import it.smartcommunitylabdhub.fsm.types.RunStateMachine;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RunManager {

    @Autowired
    RunStateMachine runStateMachine;

    @Async
    @EventListener
    public void manager(RunChangedEvent event) {
        List<RunMonitorObject> runMonitorObjects = event.getMonitorObjects();

        System.out.println("Receive runnable entity list");
        runMonitorObjects.forEach(runRunnable -> {


            // Initialize the run state machine considering current state and context

            // TODO work on state machine status change.
            // TODO GET RUN ID INITIALIZE STATE MACHINE , COMPARE RUN STATE ID WITH RUNNABLE ID
            // DELEGATE TO FSM TO DO SOMETHING.
            Fsm<State, RunEvent, Map<String, Object>> fsm = runStateMachine.create(
                    State.valueOf(runRunnable.getStateId()),
                    Map.of("runId", runRunnable.getRunId())
            );
            System.out.println(fsm.getCurrentState().name());

        });
    }
}
