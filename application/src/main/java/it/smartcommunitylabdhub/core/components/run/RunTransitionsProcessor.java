package it.smartcommunitylabdhub.core.components.run;

import it.smartcommunitylabdhub.commons.annotations.common.RunProcessorType;
import it.smartcommunitylabdhub.commons.infrastructure.RunProcessor;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import it.smartcommunitylabdhub.core.models.specs.run.RunTransitionsSpec;
import it.smartcommunitylabdhub.core.models.specs.run.RunTransitionsSpec.Transition;
import it.smartcommunitylabdhub.framework.k8s.processors.K8sProcessor;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RunProcessorType(
    stages = {
        "onBuilt",
        "onReady",
        "onRunning",
        "onCompleted",
        "onError",
        "onStopping",
        "onStopped",
        "onDeleting",
        "onDeleted",
    },
    id = K8sProcessor.ID
)
@Component(RunTransitionsProcessor.ID)
@Slf4j
public class RunTransitionsProcessor implements RunProcessor<RunTransitionsSpec> {

    public static final String ID = "transitionsProcessor";

    @Override
    public RunTransitionsSpec process(Run run, RunRunnable runRunnable, RunBaseStatus status) {
        LinkedList<RunTransitionsSpec.Transition> list = new LinkedList<>();

        //keep old in order
        Optional
            .ofNullable(run.getStatus())
            .ifPresent(s -> {
                RunTransitionsSpec rts = new RunTransitionsSpec();
                rts.configure(s);

                if (rts.getTransitions() != null) {
                    list.addAll(rts.getTransitions());
                }
            });

        //current
        Transition tr = RunTransitionsSpec.Transition
            .builder()
            .event(null) //TODO add explicit event
            .status(status != null ? status.getState() : null)
            .message(status != null ? status.getMessage() : null)
            .time(OffsetDateTime.ofInstant(Instant.now(), ZoneOffset.UTC))
            .build();

        if (log.isTraceEnabled()) {
            log.trace("transaction for {}: {}", run.getId(), tr);
        }

        //avoid duplicated events
        //TODO depends on receiving event
        //workaround, skip duplicate status
        if (list.isEmpty() || !list.get(0).getStatus().equals(tr.getStatus())) {
            list.addFirst(tr);
        }

        return RunTransitionsSpec.builder().transitions(list).build();
    }
}
