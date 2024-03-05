package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity_;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableDataItemService;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class DataItemServiceImpl implements SearchableDataItemService {

    @Autowired
    private EntityService<DataItem, DataItemEntity> entityService;

    @Autowired
    SpecRegistry specRegistry;

    @Override
    public Page<DataItem> listDataItems(Pageable pageable) {
        log.debug("list dataItems page {}", pageable);

        return entityService.list(pageable);
    }

    @Override
    public Page<DataItem> searchDataItems(Pageable pageable, SearchFilter<DataItemEntity> filter) {
        log.debug("list dataItems page {}, filter {}", pageable, String.valueOf(filter));

        Specification<DataItemEntity> specification = filter != null ? filter.toSpecification() : null;
        if (specification != null) {
            return entityService.search(specification, pageable);
        } else {
            return entityService.list(pageable);
        }
    }

    @Override
    public List<DataItem> listLatestDataItemsByProject(@NotNull String project) {
        log.debug("list dataItems for project {}", project);
        Specification<DataItemEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project)
        );

        return entityService.searchAll(specification);
    }

    @Override
    public Page<DataItem> listLatestDataItemsByProject(@NotNull String project, Pageable pageable) {
        log.debug("list dataItems for project {} page {}", project, pageable);
        Specification<DataItemEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project)
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public Page<DataItem> searchLatestDataItemsByProject(
        @NotNull String project,
        Pageable pageable,
        SearchFilter<DataItemEntity> filter
    ) {
        log.debug("list dataItems for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<DataItemEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<DataItemEntity> specification = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.latestByProject(project),
            filterSpecification
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public List<DataItem> findDataItems(@NotNull String project, @NotNull String name) {
        log.debug("find artifacts for project {} with name {}", project, name);

        //fetch all versions ordered by date DESC
        Specification<DataItemEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );

        Specification<DataItemEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(DataItemEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };

        return entityService.searchAll(specification);
    }

    @Override
    public Page<DataItem> findDataItems(@NotNull String project, @NotNull String name, Pageable pageable) {
        log.debug("find artifacts for project {} with name {} page {}", project, name, pageable);

        //fetch all versions ordered by date DESC
        Specification<DataItemEntity> where = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );
        Specification<DataItemEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(DataItemEntity_.CREATED)));
            return where.toPredicate(root, query, builder);
        };

        return entityService.search(specification, pageable);
    }

    @Override
    public DataItem findDataItem(@NotNull String id) {
        log.debug("find dataItem with id {}", String.valueOf(id));

        return entityService.find(id);
    }

    @Override
    public DataItem getDataItem(@NotNull String id) throws NoSuchEntityException {
        log.debug("get dataItem with id {}", String.valueOf(id));

        try {
            return entityService.get(id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.DATAITEM.toString());
        }
    }

    @Override
    public DataItem getLatestDataItem(@NotNull String project, @NotNull String name) throws NoSuchEntityException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLatestDataItem'");
    }

    @Override
    public DataItem createDataItem(@NotNull DataItem dto) throws DuplicatedEntityException {
        log.debug("create dataItem");
        if (log.isTraceEnabled()) {
            log.trace("dto: {}", dto);
        }

        // Parse and export Spec
        Spec spec = specRegistry.createSpec(dto.getKind(), EntityName.DATAITEM, dto.getSpec());
        if (spec == null) {
            throw new IllegalArgumentException("invalid kind");
        }

        //TODO validate

        //update spec as exported
        dto.setSpec(spec.toMap());

        try {
            if (log.isTraceEnabled()) {
                log.trace("storable dto: {}", dto);
            }

            return entityService.create(dto);
        } catch (DuplicatedEntityException e) {
            throw new DuplicatedEntityException(EntityName.DATAITEM.toString(), dto.getId());
        }
    }

    @Override
    public DataItem updateDataItem(@NotNull String id, @NotNull DataItem dataItemDTO) throws NoSuchEntityException {
        log.debug("dataItem artifact with id {}", String.valueOf(id));
        try {
            //fetch current and merge
            DataItem current = entityService.get(id);

            //spec is not modificable: enforce current
            dataItemDTO.setSpec(current.getSpec());

            //update
            return entityService.update(id, dataItemDTO);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.DATAITEM.toString());
        }
    }

    @Override
    public void deleteDataItem(@NotNull String id) {
        log.debug("delete dataItem with id {}", String.valueOf(id));

        entityService.delete(id);
    }

    @Override
    public void deleteDataItems(@NotNull String project, @NotNull String name) {
        log.debug("delete dataItems for project {} with name {}", project, name);

        Specification<DataItemEntity> spec = Specification.allOf(
            CommonSpecification.projectEquals(project),
            CommonSpecification.nameEquals(name)
        );

        long count = entityService.deleteAll(spec);
        log.debug("deleted count {}", count);
    }

    @Override
    public void deleteDataItemsByProject(@NotNull String project) {
        log.debug("delete dataItems for project {}", project);

        entityService.deleteAll(CommonSpecification.projectEquals(project));
    }
}
