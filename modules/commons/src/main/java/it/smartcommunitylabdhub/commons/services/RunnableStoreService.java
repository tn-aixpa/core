package it.smartcommunitylabdhub.commons.services;

import java.util.List;

public interface RunnableStoreService<T extends it.smartcommunitylabdhub.commons.infrastructure.Runnable> {
    T find(String id);

    void store(String id, T e);

    List<T> findAll();
}
