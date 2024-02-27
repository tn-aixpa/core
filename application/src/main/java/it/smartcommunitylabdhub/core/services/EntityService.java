package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface EntityService<D extends BaseDTO, E extends BaseEntity> {
    D create(@NotNull D dto) throws DuplicatedEntityException;
    D update(@NotNull String id, @NotNull D dto) throws NoSuchEntityException;
    void delete(@NotNull String id);
    long deleteAll(Specification<E> specification);

    D find(@NotNull String id);
    D get(@NotNull String id) throws NoSuchEntityException;

    List<D> listAll();
    Page<D> list(Pageable page);

    List<D> searchAll(Specification<E> specification);
    Page<D> search(Specification<E> specification, Pageable page);
}
