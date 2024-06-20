package it.smartcommunitylabdhub.runtime.nefertem.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.nefertem.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.run.RunNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.task.TaskMetricSpec;
import java.util.Optional;

/**
 * NefetermMetricBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "nefertem", task = "metric")
 */

public class NefertemMetricBuilder implements Builder<FunctionNefertemSpec, TaskMetricSpec, RunNefertemSpec> {

    @Override
    public RunNefertemSpec build(FunctionNefertemSpec funSpec, TaskMetricSpec taskSpec, RunNefertemSpec runSpec) {
        RunNefertemSpec runNefertemSpec = new RunNefertemSpec(runSpec.toMap());
        runNefertemSpec.setFuncSpec(funSpec);
        runNefertemSpec.setTaskMetricSpec(taskSpec);

        //let run override k8s specs
        Optional
            .ofNullable(runSpec.getTaskMetricSpec())
            .ifPresent(k8sSpec -> runNefertemSpec.getTaskMetricSpec().configure(k8sSpec.toMap()));

        // Return a run spec
        return runNefertemSpec;
    }
}
