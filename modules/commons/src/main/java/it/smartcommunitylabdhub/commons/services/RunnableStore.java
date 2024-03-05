package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.infrastructure.Runnable;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public interface RunnableStore<T extends Runnable> {
    void store(@NotNull String id, @NotNull T e) throws StoreException;

    void remove(@NotNull String id) throws StoreException;

    T find(@NotNull String id) throws StoreException;

    List<T> findAll();

    @FunctionalInterface
    interface StoreSupplier {
        <T extends Runnable> RunnableStore<T> get(Class<T> clazz);
    }
}
