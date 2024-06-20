package it.smartcommunitylabdhub.runtime.mlrun.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.mlrun.specs.function.FunctionMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.run.RunMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.task.TaskMlrunBuildSpec;
import java.util.Optional;

/**
 * MlrunMlrunBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "mlrun", task = "build")
 */

public class MlrunBuildBuilder implements Builder<FunctionMlrunSpec, TaskMlrunBuildSpec, RunMlrunSpec> {

    @Override
    public RunMlrunSpec build(FunctionMlrunSpec funSpec, TaskMlrunBuildSpec taskSpec, RunMlrunSpec runSpec) {
        RunMlrunSpec runMlrunSpec = new RunMlrunSpec(runSpec.toMap());
        runMlrunSpec.setBuildSpec(taskSpec);
        runMlrunSpec.setFuncSpec(funSpec);

        //let run override k8s specs
        Optional.ofNullable(runSpec.getJobSpec()).ifPresent(k8sSpec -> runSpec.getJobSpec().configure(k8sSpec.toMap()));

        // Return a run spec
        return runMlrunSpec;
    }
}
