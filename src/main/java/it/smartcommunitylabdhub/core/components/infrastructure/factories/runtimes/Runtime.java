package it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables.Runnable;
import it.smartcommunitylabdhub.core.models.base.RunStatus;
import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.run.Run;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;
import jakarta.validation.constraints.NotNull;

/**
 * Runtime expose builder, run and parse method
 */
public interface Runtime<F extends FunctionBaseSpec, S extends RunBaseSpec, R extends Runnable> {

    S build(

            @NotNull F funcSpec,
            @NotNull TaskBaseSpec taskSpec,
            @NotNull RunBaseSpec runSpec,
            @NotNull String kind
    );

    R run(@NotNull Run runDTO);

    // TODO: parse should get and parse result job for the given runtime.
    RunStatus parse();

}
