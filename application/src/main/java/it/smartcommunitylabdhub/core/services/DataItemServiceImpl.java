package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.DataItemService;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.DataItemEntityFilter;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class DataItemServiceImpl implements DataItemService {

    @Autowired
    private EntityService<DataItem, DataItemEntity> entityService;

    @Override
    public Page<DataItem> getDataItems(Map<String, String> filter, Pageable pageable) {
        log.debug("list dataItems with {} page {}", String.valueOf(filter), pageable);

        DataItemEntityFilter dataItemEntityFilter = new DataItemEntityFilter();
        dataItemEntityFilter.setCreatedDate(filter.get("created"));
        dataItemEntityFilter.setName(filter.get("name"));
        dataItemEntityFilter.setKind(filter.get("kind"));
        Optional<State> stateOptional = Stream
            .of(State.values())
            .filter(state -> state.name().equals(filter.get("state")))
            .findAny();
        dataItemEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

        Specification<DataItemEntity> specification = createSpecification(filter, dataItemEntityFilter);
        return entityService.search(specification, pageable);
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
    public DataItem getDataItem(@NotNull String id) throws NoSuchEntityException {
        log.debug("get dataItem with id {}", String.valueOf(id));

        try {
            return entityService.get(id);
        } catch (NoSuchEntityException e) {
            throw new NoSuchEntityException(EntityName.DATAITEM.toString());
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

    protected Specification<DataItemEntity> createSpecification(
        Map<String, String> filter,
        DataItemEntityFilter entityFilter
    ) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Add your custom filter based on the provided map
            predicate = entityFilter.toPredicate(root, query, criteriaBuilder);

            // Add more conditions for other filter if needed

            return predicate;
        };
    }
}
