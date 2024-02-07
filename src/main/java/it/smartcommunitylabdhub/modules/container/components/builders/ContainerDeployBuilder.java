package it.smartcommunitylabdhub.modules.container.components.builders;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.run.RunContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskDeploySpec;

/**
 * ContainerDeployBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "container", task = "deploy")
 */

public class ContainerDeployBuilder implements Builder<
        FunctionContainerSpec,
        TaskDeploySpec,
        RunRunSpec,
        RunContainerSpec<TaskDeploySpec>> {

    @Override
    public RunContainerSpec<TaskDeploySpec> build(
            FunctionContainerSpec funSpec,
            TaskDeploySpec taskSpec,
            RunRunSpec runSpec) {

        RunContainerSpec<TaskDeploySpec> taskDeploySpecRunContainerSpec =
                RunContainerSpec.<TaskDeploySpec>builder()
                        .taskSpec(taskSpec)
                        .funcSpec(funSpec)
                        .build();

        taskDeploySpecRunContainerSpec.configure(runSpec.toMap());

        return taskDeploySpecRunContainerSpec;
    }
}

