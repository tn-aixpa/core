package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Slf4j
@Transactional
public class BaseEntityServiceImpl<D extends BaseDTO, E extends BaseEntity> implements EntityService<D, E> {

    protected final JpaRepository<E, String> repository;

    protected final Converter<D, E> entityBuilder;
    protected final Converter<E, D> dtoBuilder;

    public BaseEntityServiceImpl(
            JpaRepository<E, String> repository,
            Converter<D, E> entityBuilder,
            Converter<E, D> dtoBuilder
    ) {
        Assert.notNull(repository, "repository can not be null");
        Assert.notNull(entityBuilder, "entity builder can not be null");
        Assert.notNull(dtoBuilder, "dto builder can not be null");

        this.repository = repository;
        this.entityBuilder = entityBuilder;
        this.dtoBuilder = dtoBuilder;
    }

    @Override
    public D create(@NotNull D dto) throws DuplicatedEntityException {
        log.debug("create with id {}", String.valueOf(dto.getId()));
        if (log.isTraceEnabled()) {
            log.trace("dto: {}", dto);
        }

        //check for existing ids
        if (dto.getId() != null && (repository.existsById(dto.getId()))) {
            throw new DuplicatedEntityException(dto.getId());
        }

        //build entity
        E entity = entityBuilder.convert(dto);

        //persist
        entity = repository.saveAndFlush(entity);
        if (log.isTraceEnabled()) {
            log.trace("entity: {}", entity);
        }

        D res = dtoBuilder.convert(entity);
        if (log.isTraceEnabled()) {
            log.trace("res: {}", res);
        }

        return res;
    }

    @Override
    public D update(@NotNull String id, @NotNull D dto) throws NoSuchEntityException {
        log.debug("update with id {}", String.valueOf(dto.getId()));
        if (log.isTraceEnabled()) {
            log.trace("dto: {}", dto);
        }

        E entity = repository.findById(id).orElseThrow(() -> new NoSuchEntityException());

        //build entity
        E e = entityBuilder.convert(dto);

        //update allowed fields
        entity.setMetadata(e.getMetadata());
        entity.setSpec(e.getSpec());
        entity.setStatus(e.getStatus());
        entity.setExtra(e.getExtra());
        entity.setState(e.getState());

        //persist
        entity = repository.saveAndFlush(entity);
        if (log.isTraceEnabled()) {
            log.trace("entity: {}", entity);
        }

        D res = dtoBuilder.convert(entity);
        if (log.isTraceEnabled()) {
            log.trace("res: {}", res);
        }

        return res;
    }

    @Override
    public void delete(@NotNull String id) {
        log.debug("delete with id {}", id);

        repository
                .findById(id)
                .ifPresent(e -> {
                    if (log.isTraceEnabled()) {
                        log.trace("entity: {}", e);
                    }

                    repository.delete(e);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public D find(@NotNull String id) {
        log.debug("find with id {}", id);

        D res = repository.findById(id).map(e -> dtoBuilder.convert(e)).orElse(null);
        if (log.isTraceEnabled()) {
            log.trace("res: {}", res);
        }

        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public D get(@NotNull String id) throws NoSuchEntityException {
        log.debug("get with id {}", id);
        D res = repository.findById(id).map(e -> dtoBuilder.convert(e)).orElseThrow(() -> new NoSuchEntityException());
        if (log.isTraceEnabled()) {
            log.trace("res: {}", res);
        }

        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<D> list(Pageable pageable) {
        log.debug("list with page {}", pageable);

        Page<E> page = repository.findAll(pageable);
        List<D> content = page.stream().map(e -> dtoBuilder.convert(e)).collect(Collectors.toList());

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public List<D> listAll() {
        log.debug("list all");

        return repository.findAll().stream().map(e -> dtoBuilder.convert(e)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<D> search(Specification<E> specification, Pageable pageable) {
        log.debug("search with spec {} page {}", specification, pageable);

        if (repository instanceof JpaSpecificationExecutor) {
            @SuppressWarnings("unchecked")
            Page<E> page = ((JpaSpecificationExecutor<E>) repository).findAll(specification, pageable);
            List<D> content = page.stream().map(e -> dtoBuilder.convert(e)).collect(Collectors.toList());

            return new PageImpl<>(content, pageable, page.getTotalElements());
        }

        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<D> searchAll(Specification<E> specification) {
        log.debug("search all with spec {} ", specification);

        if (repository instanceof JpaSpecificationExecutor) {
            return ((JpaSpecificationExecutor<E>) repository).findAll(specification)
                    .stream()
                    .map(e -> dtoBuilder.convert(e))
                    .collect(Collectors.toList());
        }

        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public long deleteAll(Specification<E> specification) {
        log.debug("delete all with spec {} ", specification);

        if (repository instanceof JpaSpecificationExecutor) {
            return ((JpaSpecificationExecutor<E>) repository).delete(specification);
        }

        throw new UnsupportedOperationException();
    }
}
