package it.smartcommunitylabdhub.commons.infrastructure;

import it.smartcommunitylabdhub.commons.models.run.Run;
import it.smartcommunitylabdhub.commons.models.run.RunBaseStatus;

@FunctionalInterface
public interface RunProcessor<Z extends RunBaseStatus> {
    Z process(Run run, RunRunnable runRunnable, RunBaseStatus status);
}
