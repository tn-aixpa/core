package it.smartcommunitylabdhub.core.utils;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.runtimes.Runtime;
import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;

public class RuntimeHelper {
    @SuppressWarnings("unchecked")
    public static <F extends FunctionBaseSpec> Runtime<F> castRuntime(Runtime<?> runtime) {
        return (Runtime<F>) runtime;
    }
}