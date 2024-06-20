package it.smartcommunitylabdhub.runtime.dbt.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.dbt.specs.function.FunctionDbtSpec;
import it.smartcommunitylabdhub.runtime.dbt.specs.run.RunDbtSpec;
import it.smartcommunitylabdhub.runtime.dbt.specs.task.TaskTransformSpec;
import java.util.Optional;

/**
 * DbtTransformBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "dbt", task = "transform")
 */

public class DbtTransformBuilder implements Builder<FunctionDbtSpec, TaskTransformSpec, RunDbtSpec> {

    @Override
    public RunDbtSpec build(FunctionDbtSpec funSpec, TaskTransformSpec taskSpec, RunDbtSpec runSpec) {
        RunDbtSpec runDbtSpec = new RunDbtSpec(runSpec.toMap());
        runDbtSpec.setTaskSpec(taskSpec);
        runDbtSpec.setFuncSpec(funSpec);

        //let run override k8s specs
        Optional
            .ofNullable(runSpec.getTaskSpec())
            .ifPresent(k8sSpec -> runDbtSpec.getTaskSpec().configure(k8sSpec.toMap()));

        return runDbtSpec;
    }
}
