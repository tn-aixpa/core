package it.smartcommunitylabdhub.commons.services.interfaces;

import java.util.List;

public interface RunnableStoreService<T> {
    T find(String id);

    void store(String id, T e);

    List<T> findAll();
}
