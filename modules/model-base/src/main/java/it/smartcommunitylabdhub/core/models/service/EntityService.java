package it.smartcommunitylabdhub.core.models.service;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EntityService<D extends BaseDTO> {
    D create(@NotNull D dto) throws DuplicatedEntityException, StoreException;
    D update(@NotNull String id, @NotNull D dto) throws NoSuchEntityException, StoreException;
    void delete(@NotNull String id) throws StoreException;

    D find(@NotNull String id) throws StoreException;
    D get(@NotNull String id) throws NoSuchEntityException, StoreException;

    List<D> listAll() throws StoreException;
    Page<D> list(Pageable page) throws StoreException;
}
