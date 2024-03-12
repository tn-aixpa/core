/**
 * FrameworkFactory.java
 * <p>
 * This class is a factory for managing and providing Frameworks (frameworks).
 */

package it.smartcommunitylabdhub.core.components.infrastructure.factories.frameworks;

import it.smartcommunitylabdhub.commons.annotations.infrastructure.FrameworkComponent;
import it.smartcommunitylabdhub.commons.infrastructure.Framework;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Deprecated
public class FrameworkFactory {

    private final Map<String, Framework<?>> builderMap;

    /**
     * Constructor to create the FrameworkFactory with a list of Frameworks.
     *
     * @param builders The list of Frameworks to be managed by the factory.
     */
    public FrameworkFactory(List<Framework<?>> builders) {
        builderMap =
            builders.stream().collect(Collectors.toMap(this::getBuilderNameFromAnnotation, Function.identity()));
    }

    /**
     * Get the framework string from the @FrameworkComponent annotation for a given Framework.
     *
     * @param builder The Framework for which to extract the framework string.
     * @return The framework string extracted from the @FrameworkComponent annotation.
     * @throws IllegalArgumentException If no @FrameworkComponent annotation is found for the
     *                                  builder.
     */
    private String getBuilderNameFromAnnotation(Framework<?> builder) {
        Class<?> builderClass = builder.getClass();
        if (builderClass.isAnnotationPresent(FrameworkComponent.class)) {
            FrameworkComponent annotation = builderClass.getAnnotation(FrameworkComponent.class);

            return annotation.framework();
        }
        throw new IllegalArgumentException(
            "No @FrameworkComponent annotation found for builder: " + builderClass.getName()
        );
    }

    /**
     * Get the Framework for the given framework.
     *
     * @param framework the framework kind.
     * @param <R>       Is a runnable that extends Runnable
     * @return The Framework for the specified framework.
     * @throws IllegalArgumentException If no Framework is found for the given framework.
     */
    @Deprecated
    public <R extends RunRunnable> Framework<R> getFramework(String framework) {
        @SuppressWarnings("unchecked")
        Framework<R> builder = (Framework<R>) builderMap.get(framework);
        if (builder == null) {
            throw new IllegalArgumentException("No builder found for name: " + framework);
        }
        return builder;
    }
}
