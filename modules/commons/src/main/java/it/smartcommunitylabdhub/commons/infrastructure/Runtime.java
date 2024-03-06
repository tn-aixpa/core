package it.smartcommunitylabdhub.commons.infrastructure;

import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.Run;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import jakarta.validation.constraints.NotNull;

/**
 * Runtime expose builder, run and parse method
 */
public interface Runtime<F extends FunctionBaseSpec, S extends RunBaseSpec, Z extends RunBaseStatus, R extends Runnable> {
    S build(@NotNull Function funcSpec, @NotNull Task taskSpec, @NotNull Run runSpec); // CREATED -> BUILT

    R run(@NotNull Run runDTO); // BUILT -> READY


    R stop(@NotNull Run runDTO); // X -> STOPPED

    Z onRunning(@NotNull Run runDTO, R runnable);

    Z onComplete(@NotNull Run runDTO, R runnable);

    Z onError(@NotNull Run runDTO, R runnable);

    Z onStopped(@NotNull Run runDTO, R runnable);
}
