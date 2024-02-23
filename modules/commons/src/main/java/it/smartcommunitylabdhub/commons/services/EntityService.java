package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.commons.models.base.BaseEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface EntityService<D extends BaseDTO, E extends BaseEntity> {
    D create(@NotNull D dto) throws NoSuchEntityException, DuplicatedEntityException;
    D update(@NotNull String id, @NotNull D dto) throws NoSuchEntityException;
    void delete(@NotNull String id);

    D find(@NotNull String id);
    D get(@NotNull String id) throws NoSuchEntityException;

    Page<D> list(Pageable page);
    Page<D> search(Specification<E> specification, Pageable page);
}
