package it.smartcommunitylabdhub.core.components.infrastructure.factories.builders;

import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.task.specs.TaskBaseSpec;


/**
 * Given a function string a task and a executionDTO return a RunDTO
 */
public interface Builder<
        F extends FunctionBaseSpec,
        T extends TaskBaseSpec,
        R extends RunBaseSpec> {
    R build(F funSpec,
            T taskSpec,
            R runSpec
    );
}
