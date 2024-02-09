package it.smartcommunitylabdhub.core.components.infrastructure.runtimes;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.infrastructure.factories.builders.Builder;
import it.smartcommunitylabdhub.commons.infrastructure.factories.runners.Runner;
import it.smartcommunitylabdhub.commons.infrastructure.factories.runtimes.Runtime;
import it.smartcommunitylabdhub.commons.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.task.specs.TaskBaseSpec;
import it.smartcommunitylabdhub.commons.utils.ErrorList;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.builders.BuilderFactory;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.runners.RunnerFactory;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Getter
public abstract class BaseRuntime<
  F extends FunctionBaseSpec, S extends RunBaseSpec, R extends Runnable
>
  implements Runtime<F, S, R> {

  protected final BuilderFactory builderFactory;
  protected final RunnerFactory runnerFactory;
  protected Map<String, ? extends Runner> runners;
  protected Map<String, ? extends Builder<? extends FunctionBaseSpec, ? extends TaskBaseSpec, ? extends RunBaseSpec>> builders;
  private String runtime;

  protected BaseRuntime(
    BuilderFactory builderFactory,
    RunnerFactory runnerFactory
  ) {
    this.builderFactory = builderFactory;
    this.runnerFactory = runnerFactory;

    // Retrieve runtime
    // Retrieve the runtime value from the subclass if it has a @RuntimeComponent annotation
    RuntimeComponent runtimeComponentAnnotation = getClass()
      .getAnnotation(RuntimeComponent.class);
    if (runtimeComponentAnnotation != null) {
      runtime = runtimeComponentAnnotation.runtime();

      // register all builders and runners
      builders = builderFactory.getBuilders(runtime);
      runners = runnerFactory.getRunners(runtime);
    } else {
      log.warn("No @RuntimeComponent annotation found on the subclass.");
    }
  }

  @SuppressWarnings("unchecked")
  public <R extends Runner> R getRunner(String task) {
    return Optional
      .ofNullable((R) runners.get(runtime + "+" + task))
      .orElseThrow(() ->
        new CoreException(
          ErrorList.INTERNAL_SERVER_ERROR.getValue(),
          "Cannot find registered Runner for <" + runtime + "+" + task + ">",
          HttpStatus.INTERNAL_SERVER_ERROR
        )
      );
  }

  @SuppressWarnings("unchecked")
  public <
    B extends Builder<? extends FunctionBaseSpec, ? extends TaskBaseSpec, ? extends RunBaseSpec>
  > B getBuilder(String task) {
    return Optional
      .ofNullable((B) builders.get(runtime + "+" + task))
      .orElseThrow(() ->
        new CoreException(
          ErrorList.INTERNAL_SERVER_ERROR.getValue(),
          "Cannot find registered Builder for <" + runtime + "+" + task + ">",
          HttpStatus.INTERNAL_SERVER_ERROR
        )
      );
  }
}
