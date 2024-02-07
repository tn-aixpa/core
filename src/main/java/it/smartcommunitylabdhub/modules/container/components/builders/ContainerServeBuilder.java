package it.smartcommunitylabdhub.modules.container.components.builders;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.run.RunContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskServeSpec;

/**
 * ContainerServeBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "container", task = "serve")
 */

public class ContainerServeBuilder implements Builder<
        FunctionContainerSpec,
        TaskServeSpec,
        RunRunSpec,
        RunContainerSpec<TaskServeSpec>> {

    @Override
    public RunContainerSpec<TaskServeSpec> build(
            FunctionContainerSpec funSpec,
            TaskServeSpec taskSpec,
            RunRunSpec runSpec) {

        RunContainerSpec<TaskServeSpec> taskServeSpecRunContainerSpec =
                RunContainerSpec.<TaskServeSpec>builder()
                        .taskSpec(taskSpec)
                        .funcSpec(funSpec)
                        .build();

        taskServeSpecRunContainerSpec.configure(runSpec.toMap());

        return taskServeSpecRunContainerSpec;
    }
}


