package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.infrastructure.Runnable;
import java.util.List;

public interface RunnableStore<T extends it.smartcommunitylabdhub.commons.infrastructure.Runnable> {
    T find(String id);

    void store(String id, T e);

    List<T> findAll();

    @FunctionalInterface
    interface StoreSupplier {
        <T extends Runnable> RunnableStore<T> get(Class<T> clazz);
    }
}
