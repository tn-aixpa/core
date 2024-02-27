package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.entities.DataItemService;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity_;
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
public class DataItemServiceImpl implements DataItemService<DataItemEntity> {

    @Autowired
    private EntityService<DataItem, DataItemEntity> entityService;

    @Override
    public Page<DataItem> listDataItems(Pageable pageable, SearchFilter<DataItemEntity> filter) {
        log.debug("list dataItems page {}, filter {}", pageable, String.valueOf(filter));

        Specification<DataItemEntity> specification = filter != null ? filter.toSpecification() : null;
        if (specification != null) {
            return entityService.search(specification, pageable);
        } else {
            return entityService.list(pageable);
        }
    }

    @Override
    public Page<DataItem> listLatestDataItemsByProject(
        @NotNull String project,
        Pageable pageable,
        SearchFilter<DataItemEntity> filter
    ) {
        log.debug("list dataItems for project {} with {} page {}", project, String.valueOf(filter), pageable);
        Specification<DataItemEntity> filterSpecification = filter != null ? filter.toSpecification() : null;
        Specification<DataItemEntity> specification = Specification.allOf(
            createProjectSpecification(project),
            CommonSpecification.latestByProject(project),
            filterSpecification
        );

        return entityService.search(specification, pageable);
    }

    @Override
    public List<DataItem> findDataItems(@NotNull String project, @NotNull String name) {
        log.debug("find artifacts for project {} with name {}", project, name);

        //fetch all versions ordered by date DESC
        Specification<DataItemEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(DataItemEntity_.CREATED)));
            return createProjectNameSpecification(project, name).toPredicate(root, query, builder);
        };

        return entityService.searchAll(specification);
    }

    @Override
    public Page<DataItem> findDataItems(@NotNull String project, @NotNull String name, Pageable pageable) {
        log.debug("find artifacts for project {} with name {} page {}", project, name, pageable);

        //fetch all versions ordered by date DESC
        Specification<DataItemEntity> specification = (root, query, builder) -> {
            query.orderBy(builder.desc(root.get(DataItemEntity_.CREATED)));
            return createProjectNameSpecification(project, name).toPredicate(root, query, builder);
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
    public DataItem createDataItem(@NotNull DataItem dataItemDTO) throws DuplicatedEntityException {
        log.debug("create dataItem");

        try {
            return entityService.create(dataItemDTO);
        } catch (DuplicatedEntityException e) {
            throw new DuplicatedEntityException(EntityName.DATAITEM.toString(), dataItemDTO.getId());
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

        long count = entityService.deleteAll(createProjectNameSpecification(project, name));
        log.debug("deleted count {}", count);
    }

    protected Specification<DataItemEntity> createProjectSpecification(String project) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(DataItemEntity_.PROJECT), project);
        };
    }

    protected Specification<DataItemEntity> createProjectNameSpecification(String project, String name) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                criteriaBuilder.equal(root.get(DataItemEntity_.PROJECT), project),
                criteriaBuilder.equal(root.get(DataItemEntity_.NAME), name)
            );
        };
    }
}
