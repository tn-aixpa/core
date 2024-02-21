package it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables;

import it.smartcommunitylabdhub.commons.infrastructure.Runnable;
import org.springframework.core.ResolvableType;

public class RunnableFactory<T extends Runnable> {

    private Class<T> clazz;

    public RunnableFactory() {
        ResolvableType resolvableType = ResolvableType.forClass(getClass());
        this.clazz = (Class<T>) resolvableType.getSuperType().getGeneric(0).resolve();
    }

    public Class<T> getType() {
        return clazz;
    }
}
