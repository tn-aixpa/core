package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.exceptions.CustomException;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.interfaces.DataItemService;
import it.smartcommunitylabdhub.core.models.builders.dataitem.DataItemDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.dataitem.DataItemEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.abstracts.AbstractSpecificationService;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.DataItemEntityFilter;
import it.smartcommunitylabdhub.core.repositories.DataItemRepository;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DataItemServiceImpl
    extends AbstractSpecificationService<DataItemEntity, DataItemEntityFilter>
    implements DataItemService {

    @Autowired
    DataItemRepository dataItemRepository;

    @Autowired
    DataItemEntityBuilder dataItemEntityBuilder;

    @Autowired
    DataItemEntityFilter dataItemEntityFilter;

    @Autowired
    DataItemDTOBuilder dataItemDTOBuilder;

    @Override
    public Page<DataItem> getDataItems(Map<String, String> filter, Pageable pageable) {
        try {
            dataItemEntityFilter.setCreatedDate(filter.get("created"));
            dataItemEntityFilter.setName(filter.get("name"));
            dataItemEntityFilter.setKind(filter.get("kind"));
            Optional<State> stateOptional = Stream
                .of(State.values())
                .filter(state -> state.name().equals(filter.get("state")))
                .findAny();
            dataItemEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<DataItemEntity> specification = createSpecification(filter, dataItemEntityFilter);

            Page<DataItemEntity> dataItemPage = this.dataItemRepository.findAll(specification, pageable);

            return new PageImpl<>(
                dataItemPage
                    .getContent()
                    .stream()
                    .map(dataItem -> dataItemDTOBuilder.build(dataItem, false))
                    .collect(Collectors.toList()),
                pageable,
                dataItemPage.getTotalElements()
            );
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DataItem createDataItem(DataItem dataItemDTO) {
        if (dataItemDTO.getId() != null && dataItemRepository.existsById(dataItemDTO.getId())) {
            throw new CoreException(
                "DuplicateDataItemId",
                "Cannot create the dataItem",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        Optional<DataItemEntity> savedDataItem = Optional
            .of(dataItemDTO)
            .map(dataItemEntityBuilder::build)
            .map(this.dataItemRepository::saveAndFlush);

        return savedDataItem
            .map(dataItem -> dataItemDTOBuilder.build(dataItem, false))
            .orElseThrow(() ->
                new CoreException("InternalServerError", "Error saving dataItem", HttpStatus.INTERNAL_SERVER_ERROR)
            );
    }

    @Override
    public DataItem getDataItem(String uuid) {
        return dataItemRepository
            .findById(uuid)
            .map(dataItem -> {
                try {
                    return dataItemDTOBuilder.build(dataItem, false);
                } catch (CustomException e) {
                    throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            })
            .orElseThrow(() ->
                new CoreException(
                    "DataItemNotFound",
                    "The dataItem you are searching for does not exist.",
                    HttpStatus.NOT_FOUND
                )
            );
    }

    @Override
    public DataItem updateDataItem(DataItem dataItemDTO, String uuid) {
        if (!dataItemDTO.getId().equals(uuid)) {
            throw new CoreException(
                "DataItemNotMatch",
                "Trying to update a DataItem with a UUID different from the one passed in the request.",
                HttpStatus.NOT_FOUND
            );
        }

        return dataItemRepository
            .findById(uuid)
            .map(dataItem -> {
                try {
                    DataItemEntity dataItemUpdated = dataItemEntityBuilder.update(dataItem, dataItemDTO);
                    dataItemRepository.saveAndFlush(dataItemUpdated);
                    return dataItemDTOBuilder.build(dataItemUpdated, false);
                } catch (CustomException e) {
                    throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            })
            .orElseThrow(() ->
                new CoreException(
                    "DataItemNotFound",
                    "The dataItem you are searching for does not exist.",
                    HttpStatus.NOT_FOUND
                )
            );
    }

    @Override
    public boolean deleteDataItem(String uuid) {
        try {
            if (this.dataItemRepository.existsById(uuid)) {
                this.dataItemRepository.deleteById(uuid);
                return true;
            }
            throw new CoreException(
                "DataItemNotFound",
                "The dataItem you are trying to delete does not exist.",
                HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            throw new CoreException("InternalServerError", "cannot delete dataItem", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
