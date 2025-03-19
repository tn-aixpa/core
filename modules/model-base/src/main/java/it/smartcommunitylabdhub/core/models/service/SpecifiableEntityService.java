package it.smartcommunitylabdhub.core.models.service;

import it.smartcommunitylabdhub.commons.exceptions.StoreException;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface SpecifiableEntityService<D extends BaseDTO, E extends BaseEntity> extends EntityService<D> {
    long deleteAll(Specification<E> specification) throws StoreException;
    List<D> searchAll(Specification<E> specification) throws StoreException;
    Page<D> search(Specification<E> specification, Pageable page) throws StoreException;
}
