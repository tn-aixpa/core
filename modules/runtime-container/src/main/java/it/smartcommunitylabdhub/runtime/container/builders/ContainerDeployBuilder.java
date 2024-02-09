package it.smartcommunitylabdhub.runtime.container.builders;

import it.smartcommunitylabdhub.commons.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.runtime.container.models.specs.function.FunctionContainerSpec;
import it.smartcommunitylabdhub.runtime.container.models.specs.run.RunContainerSpec;
import it.smartcommunitylabdhub.runtime.container.models.specs.task.TaskDeploySpec;

/**
 * ContainerDeployBuilder
 * <p>
 * You can use this as a simple class or as a registered bean. If you want to retrieve this as bean from BuilderFactory
 * you have to register it using the following annotation:
 *
 * @BuilderComponent(runtime = "container", task = "deploy")
 */

public class ContainerDeployBuilder
  implements Builder<FunctionContainerSpec, TaskDeploySpec, RunContainerSpec> {

  @Override
  public RunContainerSpec build(
    FunctionContainerSpec funSpec,
    TaskDeploySpec taskSpec,
    RunContainerSpec runSpec
  ) {
    runSpec.setTaskDeploySpec(taskSpec);
    runSpec.setFuncSpec(funSpec);

    return runSpec;
  }
}
