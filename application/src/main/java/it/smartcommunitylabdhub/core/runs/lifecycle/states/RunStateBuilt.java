package it.smartcommunitylabdhub.core.runs.lifecycle.states;

import it.smartcommunitylabdhub.authorization.model.UserAuthentication;
import it.smartcommunitylabdhub.authorization.services.CredentialsService;
import it.smartcommunitylabdhub.commons.accessors.spec.RunSpecAccessor;
import it.smartcommunitylabdhub.commons.infrastructure.ConfigurableRunnable;
import it.smartcommunitylabdhub.commons.infrastructure.Configuration;
import it.smartcommunitylabdhub.commons.infrastructure.Credentials;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.infrastructure.SecuredRunnable;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.components.security.UserAuthenticationHelper;
import it.smartcommunitylabdhub.core.runs.lifecycle.RunContext;
import it.smartcommunitylabdhub.core.runs.lifecycle.RunEvent;
import it.smartcommunitylabdhub.core.services.ConfigurationService;
import it.smartcommunitylabdhub.fsm.FsmState;
import it.smartcommunitylabdhub.fsm.Transition;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RunStateBuilt implements FsmState.Builder<State, RunEvent, RunContext, RunRunnable> {

    @Autowired
    CredentialsService credentialsService;

    @Autowired
    ConfigurationService configurationService;

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
                        UserAuthentication<?> auth = UserAuthenticationHelper.getUserAuthentication();
                        if (auth != null && r instanceof SecuredRunnable) {
                            //get credentials from providers
                            List<Credentials> credentials = credentialsService.getCredentials(
                                (UserAuthentication<?>) auth
                            );

                            ((SecuredRunnable) r).setCredentials(credentials);
                        }

                        //inject configuration if supported
                        if (r instanceof ConfigurableRunnable) {
                            List<Configuration> configurations = configurationService.getConfigurations();
                            ((ConfigurableRunnable) r).setConfigurations(configurations);
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
