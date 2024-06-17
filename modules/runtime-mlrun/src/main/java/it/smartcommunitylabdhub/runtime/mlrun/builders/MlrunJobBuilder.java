package it.smartcommunitylabdhub.runtime.mlrun.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.mlrun.specs.function.FunctionMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.run.RunMlrunSpec;
import it.smartcommunitylabdhub.runtime.mlrun.specs.task.TaskMlrunJobSpec;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * MlrunMlrunBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "mlrun", task = "job")
 */

public class MlrunJobBuilder implements Builder<FunctionMlrunSpec, TaskMlrunJobSpec, RunMlrunSpec> {

    @Override
    public RunMlrunSpec build(FunctionMlrunSpec funSpec, TaskMlrunJobSpec taskSpec, RunMlrunSpec runSpec) {
        Map<String, Serializable> map = new HashMap<>();
        map.putAll(runSpec.toMap());
        map.putAll(funSpec.toMap());
        taskSpec.toMap().forEach(map::putIfAbsent);

        RunMlrunSpec runMlrunSpec = new RunMlrunSpec(map);
        runMlrunSpec.setFuncSpec(funSpec);

        // Return a run spec
        return runMlrunSpec;
    }
}
