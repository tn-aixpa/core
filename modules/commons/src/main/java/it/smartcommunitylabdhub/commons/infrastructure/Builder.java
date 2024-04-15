package it.smartcommunitylabdhub.commons.infrastructure;

import it.smartcommunitylabdhub.commons.models.base.ExecutableBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;

/**
 * Given a execuatable entity string a task and a executionDTO return a RunDTO
 */
public interface Builder<F extends ExecutableBaseSpec, T extends TaskBaseSpec, R extends RunBaseSpec> {
    R build(F execSpec, T taskSpec, R runSpec);
}
