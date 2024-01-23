/**
 * RuntimeFactory.java
 * <p>
 * This class is a factory for managing and providing Runtimes (runtimes).
 */

package it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes;


import it.smartcommunitylabdhub.core.annotations.infrastructure.RuntimeComponent;
import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RuntimeFactory {
    private final Map<String, Runtime<? extends FunctionBaseSpec>> runtimeMap;

    /**
     * Constructor to create the RuntimeFactory with a list of Runtimes.
     *
     * @param runtimes The list of Runtimes to be managed by the factory.
     */
    public RuntimeFactory(List<Runtime<? extends FunctionBaseSpec>> runtimes) {
        runtimeMap = runtimes.stream()
                .collect(Collectors.toMap(this::getRuntimeFromAnnotation,
                        Function.identity()));
    }

    /**
     * Get the platform string from the @RuntimeComponent annotation for a given Runtime.
     *
     * @param runtime The Runtime for which to extract the platform string.
     * @return The platform string extracted from the @RuntimeComponent annotation.
     * @throws IllegalArgumentException If no @RuntimeComponent annotation is found for the
     *                                  runtime.
     */
    private String getRuntimeFromAnnotation(Runtime<? extends FunctionBaseSpec> runtime) {
        Class<?> runtimeClass = runtime.getClass();
        if (runtimeClass.isAnnotationPresent(RuntimeComponent.class)) {
            RuntimeComponent annotation =
                    runtimeClass.getAnnotation(RuntimeComponent.class);
            return annotation.runtime();
        }
        throw new IllegalArgumentException(
                "No @RuntimeComponent annotation found for runtime: "
                        + runtimeClass.getName());
    }

    /**
     * Get the Runtime for the given platform.
     *
     * @param runtime The runtime platform
     * @return The Runtime for the specified platform.
     * @throws IllegalArgumentException If no Runtime is found for the given platform.
     */
    public Runtime<? extends FunctionBaseSpec> getRuntime(String runtime) {

        Runtime<? extends FunctionBaseSpec> concreteRuntime = runtimeMap.get(runtime);
        if (concreteRuntime == null) {
            throw new IllegalArgumentException(
                    "No runtime found for name: " + runtime);
        }
        return concreteRuntime;
    }
}
