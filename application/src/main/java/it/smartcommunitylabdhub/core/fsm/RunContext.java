package it.smartcommunitylabdhub.core.fsm;

import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.models.base.ExecutableBaseSpec;
import it.smartcommunitylabdhub.commons.models.run.Run;
import it.smartcommunitylabdhub.commons.models.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.run.RunBaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class RunContext {

    public final Runtime<
        ? extends ExecutableBaseSpec,
        ? extends RunBaseSpec,
        ? extends RunBaseStatus,
        ? extends RunRunnable
    > runtime;

    public final Run run;
}
