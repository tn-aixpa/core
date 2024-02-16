package it.smartcommunitylabdhub.runtime.container.builders;

import it.smartcommunitylabdhub.commons.infrastructure.Builder;
import it.smartcommunitylabdhub.runtime.container.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.runtime.container.specs.run.RunContainerSpec;
import it.smartcommunitylabdhub.runtime.container.specs.task.TaskDeploySpec;
import java.util.Optional;

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
        RunContainerSpec containerSpec = new RunContainerSpec(runSpec.toMap());
        containerSpec.setTaskDeploySpec(taskSpec);
        containerSpec.setFunctionSpec(funSpec);

        //let run override k8s specs
        Optional
            .ofNullable(runSpec.getTaskDeploySpec())
            .ifPresent(k8sSpec -> containerSpec.getTaskDeploySpec().configure(k8sSpec.toMap()));

        return containerSpec;
    }
}
