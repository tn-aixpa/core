package it.smartcommunitylabdhub.core.services.context;

import it.smartcommunitylabdhub.commons.exceptions.CoreException;
import it.smartcommunitylabdhub.commons.exceptions.CustomException;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.builders.dataitem.DataItemDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.dataitem.DataItemEntityBuilder;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.DataItemEntityFilter;
import it.smartcommunitylabdhub.core.models.queries.specifications.CommonSpecification;
import it.smartcommunitylabdhub.core.repositories.DataItemRepository;
import it.smartcommunitylabdhub.core.services.context.interfaces.DataItemContextService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class DataItemContextServiceImpl
        extends ContextService<DataItemEntity, DataItemEntityFilter>
        implements DataItemContextService {

    @Autowired
    DataItemRepository dataItemRepository;

    @Autowired
    DataItemDTOBuilder dataItemDTOBuilder;

    @Autowired
    DataItemEntityFilter dataItemEntityFilter;

    @Autowired
    DataItemEntityBuilder dataItemEntityBuilder;

    @Override
    public DataItem createDataItem(String projectName, DataItem dataItemDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // dataItemDTO
            if (!projectName.equals(dataItemDTO.getProject())) {
                throw new CustomException("Project Context and DataItem Project does not match", null);
            }

            // Check project context
            checkContext(dataItemDTO.getProject());

            // Check if dataItem already exist if exist throw exception otherwise create a
            // new one
            DataItemEntity dataItem = (DataItemEntity) Optional
                    .ofNullable(dataItemDTO.getId())
                    .flatMap(id ->
                            dataItemRepository
                                    .findById(id)
                                    .map(a -> {
                                        throw new CustomException(
                                                "The project already contains an dataItem with the specified UUID.",
                                                null
                                        );
                                    })
                    )
                    .orElseGet(() -> {
                        // Build an dataItem and store it in the database
                        DataItemEntity newDataItem = dataItemEntityBuilder.build(dataItemDTO);
                        return dataItemRepository.saveAndFlush(newDataItem);
                    });

            // Return dataItem DTO
            return dataItemDTOBuilder.build(dataItem, dataItem.getEmbedded());
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<DataItem> getLatestByProjectName(Map<String, String> filter, String projectName, Pageable pageable) {
        try {
            checkContext(projectName);

            dataItemEntityFilter.setCreatedDate(filter.get("created"));
            dataItemEntityFilter.setName(filter.get("name"));
            dataItemEntityFilter.setKind(filter.get("kind"));

            Optional<State> stateOptional = Stream
                    .of(State.values())
                    .filter(state -> state.name().equals(filter.get("state")))
                    .findAny();

            dataItemEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<DataItemEntity> specification = createSpecification(filter, dataItemEntityFilter)
                    .and(CommonSpecification.latestByProject(projectName));

            Page<DataItemEntity> dataItemPage = dataItemRepository.findAll(
                    Specification
                            .where(specification)
                            .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("project"), projectName)),
                    pageable
            );

            return new PageImpl<>(
                    dataItemPage
                            .getContent()
                            .stream()
                            .map(dataItem -> {
                                return dataItemDTOBuilder.build(dataItem, dataItem.getEmbedded());
                            })
                            .collect(Collectors.toList()),
                    pageable,
                    dataItemPage.getTotalElements()
            );
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<DataItem> getByProjectNameAndDataItemName(
            Map<String, String> filter,
            String projectName,
            String dataItemName,
            Pageable pageable
    ) {
        try {
            checkContext(projectName);

            dataItemEntityFilter.setCreatedDate(filter.get("created"));
            dataItemEntityFilter.setKind(filter.get("kind"));
            Optional<State> stateOptional = Stream
                    .of(State.values())
                    .filter(state -> state.name().equals(filter.get("state")))
                    .findAny();

            dataItemEntityFilter.setState(stateOptional.map(Enum::name).orElse(null));

            Specification<DataItemEntity> specification = createSpecification(filter, dataItemEntityFilter);

            Page<DataItemEntity> dataItemPage = dataItemRepository.findAll(
                    Specification
                            .where(specification)
                            .and((root, query, criteriaBuilder) ->
                                    criteriaBuilder.and(
                                            criteriaBuilder.equal(root.get("project"), projectName),
                                            criteriaBuilder.equal(root.get("name"), dataItemName)
                                    )
                            ),
                    pageable
            );

            return new PageImpl<>(
                    dataItemPage
                            .getContent()
                            .stream()
                            .map(dataItem -> {
                                return dataItemDTOBuilder.build(dataItem, dataItem.getEmbedded());
                            })
                            .collect(Collectors.toList()),
                    pageable,
                    dataItemPage.getTotalElements()
            );
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DataItem getByProjectAndDataItemAndUuid(String projectName, String dataItemName, String uuid) {
        try {
            // Check project context
            checkContext(projectName);

            return this.dataItemRepository.findByProjectAndNameAndId(projectName, dataItemName, uuid)
                    .map(dataItem -> dataItemDTOBuilder.build(dataItem, dataItem.getEmbedded()))
                    .orElseThrow(() -> new CustomException("The dataItem does not exist.", null));
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DataItem getLatestByProjectNameAndDataItemName(String projectName, String dataItemName) {
        try {
            // Check project context
            checkContext(projectName);

            return this.dataItemRepository.findLatestDataItemByProjectAndName(projectName, dataItemName)
                    .map(dataItem -> dataItemDTOBuilder.build(dataItem, dataItem.getEmbedded()))
                    .orElseThrow(() -> new CustomException("The dataItem does not exist.", null));
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DataItem createOrUpdateDataItem(String projectName, String dataItemName, DataItem dataItemDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // dataItemDTO
            if (!projectName.equals(dataItemDTO.getProject())) {
                throw new CustomException("Project Context and DataItem Project does not match.", null);
            }
            if (!dataItemName.equals(dataItemDTO.getName())) {
                throw new CustomException(
                        "Trying to create/update an dataItem with name different from the one passed in the request.",
                        null
                );
            }

            // Check project context
            checkContext(dataItemDTO.getProject());

            // Check if dataItem already exist if exist throw exception otherwise create a
            // new one
            DataItemEntity dataItem = Optional
                    .ofNullable(dataItemDTO.getId())
                    .flatMap(id -> {
                        Optional<DataItemEntity> optionalDataItem = dataItemRepository.findById(id);
                        if (optionalDataItem.isPresent()) {
                            DataItemEntity existingDataItem = optionalDataItem.get();

                            // Update the existing dataItem version
                            final DataItemEntity dataItemUpdated = dataItemEntityBuilder.update(
                                    existingDataItem,
                                    dataItemDTO
                            );
                            return Optional.of(this.dataItemRepository.saveAndFlush(dataItemUpdated));
                        } else {
                            // Build a new dataItem and store it in the database
                            DataItemEntity newDataItem = dataItemEntityBuilder.build(dataItemDTO);
                            return Optional.of(dataItemRepository.saveAndFlush(newDataItem));
                        }
                    })
                    .orElseGet(() -> {
                        // Build a new dataItem and store it in the database
                        DataItemEntity newDataItem = dataItemEntityBuilder.build(dataItemDTO);
                        return dataItemRepository.saveAndFlush(newDataItem);
                    });

            // Return dataItem DTO
            return dataItemDTOBuilder.build(dataItem, dataItem.getEmbedded());
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public DataItem updateDataItem(String projectName, String dataItemName, String uuid, DataItem dataItemDTO) {
        try {
            // Check that project context is the same as the project passed to the
            // dataItemDTO
            if (!projectName.equals(dataItemDTO.getProject())) {
                throw new CustomException("Project Context and DataItem Project does not match", null);
            }
            if (!uuid.equals(dataItemDTO.getId())) {
                throw new CustomException(
                        "Trying to update an dataItem with an ID different from the one passed in the request.",
                        null
                );
            }
            // Check project context
            checkContext(dataItemDTO.getProject());

            DataItemEntity dataItem =
                    this.dataItemRepository.findById(dataItemDTO.getId())
                            .map(a -> {
                                // Update the existing dataItem version
                                return dataItemEntityBuilder.update(a, dataItemDTO);
                            })
                            .orElseThrow(() -> new CustomException("The dataItem does not exist.", null));

            // Return dataItem DTO
            return dataItemDTOBuilder.build(dataItem, dataItem.getEmbedded());
        } catch (CustomException e) {
            throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public Boolean deleteSpecificDataItemVersion(String projectName, String dataItemName, String uuid) {
        try {
            if (this.dataItemRepository.existsByProjectAndNameAndId(projectName, dataItemName, uuid)) {
                this.dataItemRepository.deleteByProjectAndNameAndId(projectName, dataItemName, uuid);
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

    @Override
    @Transactional
    public Boolean deleteAllDataItemVersions(String projectName, String dataItemName) {
        try {
            if (dataItemRepository.existsByProjectAndName(projectName, dataItemName)) {
                this.dataItemRepository.deleteByProjectAndName(projectName, dataItemName);
                return true;
            }
            throw new CoreException(
                    "DataItemNotFound",
                    "The dataItems you are trying to delete does not exist.",
                    HttpStatus.NOT_FOUND
            );
        } catch (Exception e) {
            throw new CoreException("InternalServerError", "cannot delete dataItem", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
