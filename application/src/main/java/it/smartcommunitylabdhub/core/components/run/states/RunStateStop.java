package it.smartcommunitylabdhub.core.components.run.states;

import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.fsm.RunContext;
import it.smartcommunitylabdhub.core.fsm.RunEvent;
import it.smartcommunitylabdhub.fsm.FsmState;
import it.smartcommunitylabdhub.fsm.Transition;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RunStateStop implements FsmState.Builder<State, RunEvent, RunContext, RunRunnable> {

    public FsmState<State, RunEvent, RunContext, RunRunnable> build() {
        //define state
        State state = State.STOP;

        //transitions
        List<Transition<State, RunEvent, RunContext, RunRunnable>> txs = List.of(
            //(STOP)->STOPPED
            new Transition.Builder<State, RunEvent, RunContext, RunRunnable>()
                .event(RunEvent.STOP)
                .nextState(State.STOPPED)
                .withInternalLogic((currentState, nextState, event, context, runnable) -> {
                    //runtime callback
                    return Optional.ofNullable(context.runtime.onStopped(context.run, runnable));
                })
                .build(),
            //(ERROR)->ERROR
            new Transition.Builder<State, RunEvent, RunContext, RunRunnable>()
                .event(RunEvent.ERROR)
                .nextState(State.ERROR)
                .withInternalLogic((currentState, nextState, event, context, runnable) -> {
                    RunSpecAccessor specAccessor = RunSpecAccessor.with(context.run.getSpec());
                    if (specAccessor.isLocalExecution()) {
                        return Optional.empty();
                    }

                    //error callback
                    return Optional.ofNullable(context.runtime.onError(context.run, runnable));
                })
                .build(),
            //(DELETING)->DELETING
            new Transition.Builder<State, RunEvent, RunContext, RunRunnable>()
                .event(RunEvent.DELETING)
                .nextState(State.DELETING)
                .withInternalLogic((currentState, nextState, event, context, runnable) -> {
                    RunSpecAccessor specAccessor = RunSpecAccessor.with(context.run.getSpec());
                    if (specAccessor.isLocalExecution()) {
                        return Optional.empty();
                    }

                    //delete via runtime
                    return Optional.ofNullable(context.runtime.delete(context.run));
                })
                .build()
        );

        return new FsmState<>(state, txs);
    }
}
