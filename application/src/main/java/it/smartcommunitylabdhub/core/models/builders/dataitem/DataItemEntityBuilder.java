package it.smartcommunitylabdhub.core.models.builders.dataitem;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItemMetadata;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DataItemEntityBuilder implements Converter<DataItem, DataItemEntity> {

    private final CBORConverter cborConverter;

    public DataItemEntityBuilder(CBORConverter cborConverter) {
        this.cborConverter = cborConverter;
    }

    /**
     * Build a dataItem from a dataItemDTO and store extra values as d cbor
     * <p>
     *
     * @return DataItemDTO
     */
    public DataItemEntity build(DataItem dto) {
        // Retrieve field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        DataItemMetadata metadata = new DataItemMetadata();
        metadata.configure(dto.getMetadata());

        return DataItemEntity
            .builder()
            .id(dto.getId())
            .name(dto.getName())
            .kind(dto.getKind())
            .project(dto.getProject())
            .metadata(cborConverter.convert(dto.getMetadata()))
            .spec(cborConverter.convert(dto.getSpec()))
            .status(cborConverter.convert(dto.getStatus()))
            .extra(cborConverter.convert(dto.getExtra()))
            .state(
                // Store status if not present
                statusFieldAccessor.getState() == null ? State.CREATED : State.valueOf(statusFieldAccessor.getState())
            )
            // Metadata Extraction
            .embedded(metadata.getEmbedded() == null ? Boolean.FALSE : metadata.getEmbedded())
            .created(metadata.getCreated())
            .updated(metadata.getUpdated())
            .build();
    }

    @Override
    public DataItemEntity convert(DataItem source) {
        return build(source);
    }
}
