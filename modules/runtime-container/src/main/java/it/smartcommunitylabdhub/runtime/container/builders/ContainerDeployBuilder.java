package it.smartcommunitylabdhub.runtime.container.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.container.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.runtime.container.specs.run.RunContainerSpec;
import it.smartcommunitylabdhub.runtime.container.specs.task.TaskDeploySpec;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ContainerDeployBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "container", task = "deploy")
 */

public class ContainerDeployBuilder implements Builder<FunctionContainerSpec, TaskDeploySpec, RunContainerSpec> {

    @Override
    public RunContainerSpec build(FunctionContainerSpec funSpec, TaskDeploySpec taskSpec, RunContainerSpec runSpec) {
        Map<String, Serializable> map = new HashMap<>();
        map.putAll(runSpec.toMap());
        map.putAll(funSpec.toMap());
        taskSpec.toMap().forEach(map::putIfAbsent);

        RunContainerSpec runContainerSpec = new RunContainerSpec(map);
        runContainerSpec.setFuncSpec(funSpec);

        return runContainerSpec;
    }
}
