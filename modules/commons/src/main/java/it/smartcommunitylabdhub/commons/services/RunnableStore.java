package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.infrastructure.RunRunnable;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface RunnableStore<T extends RunRunnable> {
    void store(@NotNull String id, @NotNull T e) throws StoreException;

    void remove(@NotNull String id) throws StoreException;

    T find(@NotNull String id) throws StoreException;

    List<T> findAll();

    @FunctionalInterface
    interface StoreSupplier {
        <T extends RunRunnable> RunnableStore<T> get(Class<T> clazz);
    }
}
