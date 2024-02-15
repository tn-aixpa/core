package it.smartcommunitylabdhub.core.models.builders.dataitem;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItemMetadata;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import java.io.Serializable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DataItemEntityBuilder implements Converter<DataItem, DataItemEntity> {

    @Autowired
    CBORConverter cborConverter;

    @Autowired
    SpecRegistry specRegistry;

    /**
     * Build a dataItem from a dataItemDTO and store extra values as d cbor
     * <p>
     *
     * @return DataItemDTO
     */
    public DataItemEntity build(DataItem dto) {
        // Parse and export Spec
        Map<String, Serializable> spec = specRegistry
            .createSpec(dto.getKind(), EntityName.DATAITEM, dto.getSpec())
            .toMap();

        // Retrieve field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        DataItemMetadata metadata = new DataItemMetadata();
        metadata.configure(dto.getMetadata());

        return EntityFactory.combine(
            DataItemEntity.builder().build(),
            dto,
            builder ->
                builder
                    .withIf(dto.getId() != null, e -> e.setId(dto.getId()))
                    .with(e -> e.setName(dto.getName()))
                    .with(e -> e.setKind(dto.getKind()))
                    .with(e -> e.setProject(dto.getProject()))
                    .with(e -> e.setMetadata(cborConverter.convert(dto.getMetadata())))
                    .with(e -> e.setSpec(cborConverter.convert(spec)))
                    .with(e -> e.setStatus(cborConverter.convert(dto.getStatus())))
                    .with(e -> e.setExtra(cborConverter.convert(dto.getExtra())))
                    // Store status if not present
                    .withIfElse(
                        (statusFieldAccessor.getState() == null),
                        (d, condition) -> {
                            if (condition) {
                                d.setState(State.CREATED);
                            } else {
                                d.setState(State.valueOf(statusFieldAccessor.getState()));
                            }
                        }
                    )
                    // Metadata Extraction
                    .withIfElse(
                        metadata.getEmbedded() == null,
                        (a, condition) -> {
                            if (condition) {
                                a.setEmbedded(false);
                            } else {
                                a.setEmbedded(metadata.getEmbedded());
                            }
                        }
                    )
                    .withIf(metadata.getCreated() != null, e -> e.setCreated(metadata.getCreated()))
                    .withIf(metadata.getUpdated() != null, e -> e.setUpdated(metadata.getUpdated()))
        );
    }

    @Override
    public DataItemEntity convert(DataItem source) {
        return build(source);
    }

    /**
     * Update a dataItem if element is not passed it override causing empty field
     *
     * @param dataItem    the Dataitem
     * @param dataItemDTO the Dataitem DTO to combine
     * @return Dataitem
     */
    public DataItemEntity update(DataItemEntity dataItem, DataItem dataItemDTO) {
        DataItemEntity newDataItem = build(dataItemDTO);
        return doUpdate(dataItem, newDataItem);
    }

    private DataItemEntity doUpdate(DataItemEntity dataItem, DataItemEntity newDataItem) {
        return EntityFactory.combine(
            dataItem,
            newDataItem,
            builder ->
                builder
                    .withIfElse(
                        newDataItem.getState().name().equals(State.NONE.name()),
                        (d, condition) -> {
                            if (condition) {
                                d.setState(State.CREATED);
                            } else {
                                d.setState(newDataItem.getState());
                            }
                        }
                    )
                    .with(e -> e.setMetadata(newDataItem.getMetadata()))
                    .with(e -> e.setExtra(newDataItem.getExtra()))
                    .with(e -> e.setStatus(newDataItem.getStatus()))
                    // Metadata Extraction
                    .withIfElse(
                        newDataItem.getEmbedded() == null,
                        (d, condition) -> {
                            if (condition) {
                                d.setEmbedded(false);
                            } else {
                                d.setEmbedded(newDataItem.getEmbedded());
                            }
                        }
                    )
        );
    }
}
