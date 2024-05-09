package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface EntityService<D extends BaseDTO, E extends BaseEntity> {
    D create(@NotNull D dto) throws DuplicatedEntityException, StoreException;
    D update(@NotNull String id, @NotNull D dto) throws NoSuchEntityException, StoreException;
    void delete(@NotNull String id) throws StoreException;
    long deleteAll(Specification<E> specification) throws StoreException;

    D find(@NotNull String id) throws StoreException;
    D get(@NotNull String id) throws NoSuchEntityException, StoreException;

    List<D> listAll() throws StoreException;
    Page<D> list(Pageable page) throws StoreException;

    List<D> searchAll(Specification<E> specification) throws StoreException;
    Page<D> search(Specification<E> specification, Pageable page) throws StoreException;
}
