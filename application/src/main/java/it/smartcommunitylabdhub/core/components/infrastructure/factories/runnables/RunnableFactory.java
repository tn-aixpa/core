package it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables;

import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import org.springframework.core.ResolvableType;

public class RunnableFactory<T extends RunRunnable> {

    private Class<T> clazz;

    @SuppressWarnings("unchecked")
    public RunnableFactory() {
        ResolvableType resolvableType = ResolvableType.forClass(getClass());
        this.clazz = (Class<T>) resolvableType.getSuperType().getGeneric(0).resolve();
    }

    public Class<T> getType() {
        return clazz;
    }
}
