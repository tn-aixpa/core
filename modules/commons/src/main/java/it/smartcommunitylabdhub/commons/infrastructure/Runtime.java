package it.smartcommunitylabdhub.commons.infrastructure;

import it.smartcommunitylabdhub.commons.models.base.RunStatus;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.task.TaskBaseSpec;
import jakarta.validation.constraints.NotNull;

/**
 * Runtime expose builder, run and parse method
 */
public interface Runtime<F extends FunctionBaseSpec, S extends RunBaseSpec, R extends Runnable> {
    S build(@NotNull F funcSpec, @NotNull TaskBaseSpec taskSpec, @NotNull RunBaseSpec runSpec, @NotNull String kind);

    R run(@NotNull Run runDTO);

    // TODO: parse should get and parse result job for the given runtime.
    RunStatus parse();
}
