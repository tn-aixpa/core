package it.smartcommunitylabdhub.core.components.run.states;

import it.smartcommunitylabdhub.authorization.services.JwtTokenService;
import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.config.SecurityProperties;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.infrastructure.SecuredRunnable;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.fsm.RunContext;
import it.smartcommunitylabdhub.core.fsm.RunEvent;
import it.smartcommunitylabdhub.fsm.FsmState;
import it.smartcommunitylabdhub.fsm.Transition;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RunStateBuilt implements FsmState.Builder<State, RunEvent, RunContext, RunRunnable> {

    @Autowired
    JwtTokenService jwtTokenService;

    @Autowired
    SecurityProperties securityProperties;

    public FsmState<State, RunEvent, RunContext, RunRunnable> build() {
        //define state
        State state = State.BUILT;

        //transitions
        List<Transition<State, RunEvent, RunContext, RunRunnable>> txs = List.of(
            //(RUN)->READY
            new Transition.Builder<State, RunEvent, RunContext, RunRunnable>()
                .event(RunEvent.RUN)
                .nextState(State.READY)
                .withInternalLogic((currentState, nextState, event, context, rn) -> {
                    RunSpecAccessor specAccessor = RunSpecAccessor.with(context.run.getSpec());
                    if (specAccessor.isLocalExecution()) {
                        return Optional.empty();
                    }

                    //run via runtime
                    Optional<RunRunnable> runnable = Optional.ofNullable(context.runtime.run(context.run));
                    runnable.ifPresent(r -> {
                        //extract auth from security context to inflate secured credentials
                        //TODO refactor properly
                        if (r instanceof SecuredRunnable) {
                            // check that auth is enabled via securityProperties
                            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                            if (auth != null && securityProperties.isRequired()) {
                                Serializable credentials = jwtTokenService.generateCredentials(auth);
                                if (credentials != null) {
                                    ((SecuredRunnable) r).setCredentials(credentials);
                                }
                            }
                        }
                    });

                    return runnable;
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
                    //no-op, nothing happened yet
                    return Optional.empty();
                })
                .build()
        );

        return new FsmState<>(state, txs);
    }
}
