package it.smartcommunitylabdhub.runtime.nefertem.builders;

import it.smartcommunitylabdhub.commons.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.run.RunNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.task.TaskProfileSpec;

/**
 * NefetermProfileBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "nefertem", task = "profile")
 */

public class NefertemProfileBuilder implements Builder<FunctionNefertemSpec, TaskProfileSpec, RunNefertemSpec> {

    @Override
    public RunNefertemSpec build(FunctionNefertemSpec funSpec, TaskProfileSpec taskSpec, RunNefertemSpec runSpec) {
        RunNefertemSpec runNefertemSpec = RunNefertemSpec.builder().build();

        runNefertemSpec.configure(runSpec.toMap());
        runNefertemSpec.setFuncSpec(funSpec);
        runNefertemSpec.setTaskProfileSpec(taskSpec);

        // Return a run spec
        return runNefertemSpec;
    }
}
