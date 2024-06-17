package it.smartcommunitylabdhub.runtime.nefertem.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.nefertem.specs.function.FunctionNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.run.RunNefertemSpec;
import it.smartcommunitylabdhub.runtime.nefertem.specs.task.TaskMetricSpec;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
        Map<String, Serializable> map = new HashMap<>();
        map.putAll(runSpec.toMap());
        map.putAll(funSpec.toMap());
        taskSpec.toMap().forEach(map::putIfAbsent);

        RunNefertemSpec runNefertemSpec = new RunNefertemSpec(map);
        runNefertemSpec.setFuncSpec(funSpec);

        // Return a run spec
        return runNefertemSpec;
    }
}
