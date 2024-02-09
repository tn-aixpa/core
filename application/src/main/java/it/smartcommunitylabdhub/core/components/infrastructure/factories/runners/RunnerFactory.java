/**
 * RunnerFactory.java
 * <p>
 * This class is a factory for managing and providing Runners (runners).
 */

package it.smartcommunitylabdhub.core.components.infrastructure.factories.runners;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.RunnerComponent;
import it.smartcommunitylabdhub.commons.infrastructure.factories.runners.Runner;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RunnerFactory {

  private final Map<String, ? extends Runner> runnerMap;

  /**
   * Constructor to create the RunnerFactory with a list of Runners.
   *
   * @param runners The list of Runners to be managed by the factory.
   */
  public RunnerFactory(List<? extends Runner> runners) {
    runnerMap =
      runners
        .stream()
        .collect(
          Collectors.toMap(this::getRunnerFromAnnotation, Function.identity())
        );
  }

  /**
   * Get the platform string from the @RunnerComponent annotation for a given Runner.
   *
   * @param runner The Runner for which to extract the platform string.
   * @return The platform string extracted from the @RunnerComponent annotation.
   * @throws IllegalArgumentException If no @RunnerComponent annotation is found for the
   *                                  runner.
   */
  private String getRunnerFromAnnotation(Runner runner) {
    Class<?> runnerClass = runner.getClass();
    if (runnerClass.isAnnotationPresent(RunnerComponent.class)) {
      RunnerComponent annotation = runnerClass.getAnnotation(
        RunnerComponent.class
      );
      return annotation.runtime() + "+" + annotation.task();
    }
    throw new IllegalArgumentException(
      "No @RunnerComponent annotation found for runner: " +
      runnerClass.getName()
    );
  }

  /**
   * Get the Runner for the given platform.
   *
   * @param runtime The runner platform
   * @param task    The task
   * @return The Runner for the specified platform.
   * @throws IllegalArgumentException If no Runner is found for the given platform.
   */

  @SuppressWarnings("unchecked")
  public <R extends Runner> R getRunner(String runtime, String task) {
    R concreteRunner = (R) runnerMap.get(runtime + "+" + task);
    if (concreteRunner == null) {
      throw new IllegalArgumentException(
        "No runner found for name: " + runtime + "+" + task
      );
    }
    return concreteRunner;
  }

  public Map<String, ? extends Runner> getRunners(String runtime) {
    return runnerMap
      .entrySet()
      .stream()
      .filter(entry -> entry.getKey().startsWith(runtime))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
