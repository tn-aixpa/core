package it.smartcommunitylabdhub.core.triggers.lifecycle;

import it.smartcommunitylabdhub.commons.infrastructure.TriggerRun;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerEvent;
import it.smartcommunitylabdhub.commons.models.trigger.TriggerJob;
import it.smartcommunitylabdhub.core.fsm.AbstractFsmFactory;
import it.smartcommunitylabdhub.fsm.FsmState;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TriggerFsmFactory
    extends AbstractFsmFactory<State, TriggerEvent, TriggerContext, TriggerRun<? extends TriggerJob>> {

    public TriggerFsmFactory(
        List<FsmState.Builder<State, TriggerEvent, TriggerContext, TriggerRun<? extends TriggerJob>>> stateBuilders
    ) {
        super(stateBuilders);
    }
}
