package it.smartcommunitylabdhub.core.triggers.lifecycle;

import it.smartcommunitylabdhub.commons.infrastructure.TriggerRun;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerEvent;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerJob;
import it.smartcommunitylabdhub.fsm.FsmState;
import it.smartcommunitylabdhub.fsm.Transition;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TriggerStateStopped
    implements FsmState.Builder<State, TriggerEvent, TriggerContext, TriggerRun<? extends TriggerJob>> {

    public FsmState<State, TriggerEvent, TriggerContext, TriggerRun<? extends TriggerJob>> build() {
        //define state
        State state = State.STOPPED;

        //transitions
        List<Transition<State, TriggerEvent, TriggerContext, TriggerRun<? extends TriggerJob>>> txs = List.of(
            //(RUN)->RUNNING
            new Transition.Builder<State, TriggerEvent, TriggerContext, TriggerRun<? extends TriggerJob>>()
                .event(TriggerEvent.RUN)
                .nextState(State.RUNNING)
                .withInternalLogic((currentState, nextState, event, context, trigger) -> {
                    //run callback
                    return Optional.ofNullable(context.actuator.run(context.trigger));
                })
                .build(),
            new Transition.Builder<State, TriggerEvent, TriggerContext, TriggerRun<? extends TriggerJob>>()
                .event(TriggerEvent.ERROR)
                .nextState(State.ERROR)
                .withInternalLogic((currentState, nextState, event, context, runnable) -> {
                    //no-op, nothing happened yet
                    return Optional.empty();
                })
                .build()
        );

        return new FsmState<>(state, txs);
    }
}
