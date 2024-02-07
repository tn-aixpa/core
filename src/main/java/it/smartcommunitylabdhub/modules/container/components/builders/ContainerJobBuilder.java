package it.smartcommunitylabdhub.modules.container.components.builders;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.run.RunContainerSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.task.TaskJobSpec;

/**
 * ContainerJobBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "container", task = "job")
 */
public class ContainerJobBuilder implements Builder<
        FunctionContainerSpec,
        TaskJobSpec,
        RunRunSpec,
        RunContainerSpec<TaskJobSpec>> {

    @Override
    public RunContainerSpec<TaskJobSpec> build(
            FunctionContainerSpec funSpec,
            TaskJobSpec taskSpec,
            RunRunSpec runSpec) {

        RunContainerSpec<TaskJobSpec> taskJobSpecRunContainerSpec =
                RunContainerSpec.<TaskJobSpec>builder()
                        .taskContainerSpec(taskSpec)
                        .functionContainerSpec(funSpec)
                        .build();

        taskJobSpecRunContainerSpec.configure(runSpec.toMap());

        return taskJobSpecRunContainerSpec;
    }
}

