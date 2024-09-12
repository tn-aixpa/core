package it.smartcommunitylabdhub.core.components.infrastructure.runtimes;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import it.smartcommunitylabdhub.commons.infrastructure.Runtime;
import it.smartcommunitylabdhub.commons.models.base.ExecutableBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseStatus;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RuntimeFactory {

    private final Map<
        String,
        Runtime<? extends ExecutableBaseSpec, ? extends RunBaseSpec, ? extends RunBaseStatus, ? extends RunRunnable>
    > runtimeMap;

    /**
     * Constructor to create the RuntimeFactory with a list of Runtimes.
     *
     * @param runtimes The list of Runtimes to be managed by the factory.
     */
    public RuntimeFactory(
        List<
            Runtime<? extends ExecutableBaseSpec, ? extends RunBaseSpec, ? extends RunBaseStatus, ? extends RunRunnable>
        > runtimes
    ) {
        runtimeMap = runtimes.stream().collect(Collectors.toMap(this::getRuntimeFromAnnotation, Function.identity()));
    }

    /**
     * Get the platform string from the @RuntimeComponent annotation for a given Runtime.
     *
     * @param runtime The Runtime for which to extract the platform string.
     * @return The platform string extracted from the @RuntimeComponent annotation.
     * @throws IllegalArgumentException If no @RuntimeComponent annotation is found for the
     *                                  runtime.
     */
    private String getRuntimeFromAnnotation(
        Runtime<
            ? extends ExecutableBaseSpec,
            ? extends RunBaseSpec,
            ? extends RunBaseStatus,
            ? extends RunRunnable
        > runtime
    ) {
        Class<?> runtimeClass = runtime.getClass();
        if (runtimeClass.isAnnotationPresent(RuntimeComponent.class)) {
            RuntimeComponent annotation = runtimeClass.getAnnotation(RuntimeComponent.class);
            return annotation.runtime();
        }
        throw new IllegalArgumentException(
            "No @RuntimeComponent annotation found for runtime: " + runtimeClass.getName()
        );
    }

    /**
     * Get the Runtime for the given platform.
     *
     * @param runtime The runtime platform
     * @return The Runtime for the specified platform.
     * @throws IllegalArgumentException If no Runtime is found for the given platform.
     */
    public Runtime<
        ? extends ExecutableBaseSpec,
        ? extends RunBaseSpec,
        ? extends RunBaseStatus,
        ? extends RunRunnable
    > getRuntime(String runtime) {
        Runtime<
            ? extends ExecutableBaseSpec,
            ? extends RunBaseSpec,
            ? extends RunBaseStatus,
            ? extends RunRunnable
        > concreteRuntime = runtimeMap.get(runtime);
        if (concreteRuntime == null) {
            throw new IllegalArgumentException("No runtime found for name: " + runtime);
        }
        return concreteRuntime;
    }
}
