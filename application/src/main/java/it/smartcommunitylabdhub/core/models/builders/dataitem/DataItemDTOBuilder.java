package it.smartcommunitylabdhub.core.models.builders.dataitem;

import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.metadata.DataItemMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.converters.types.MetadataConverter;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DataItemDTOBuilder {

  @Autowired
  MetadataConverter<DataItemMetadata> metadataConverter;

  public DataItem build(DataItemEntity dataItem, boolean embeddable) {
    return EntityFactory.create(
      DataItem::new,
      dataItem,
      builder ->
        builder
          .with(dto -> dto.setId(dataItem.getId()))
          .with(dto -> dto.setKind(dataItem.getKind()))
          .with(dto -> dto.setProject(dataItem.getProject()))
          .with(dto -> dto.setName(dataItem.getName()))
          .with(dto -> {
            // Set Metadata for dataItem
            DataItemMetadata dataItemMetadata = Optional
              .ofNullable(
                metadataConverter.reverseByClass(
                  dataItem.getMetadata(),
                  DataItemMetadata.class
                )
              )
              .orElseGet(DataItemMetadata::new);

            if (!StringUtils.hasText(dataItemMetadata.getVersion())) {
              dataItemMetadata.setVersion(dataItem.getId());
            }
            if (!StringUtils.hasText(dataItemMetadata.getName())) {
              dataItemMetadata.setName(dataItem.getName());
            }

            dataItemMetadata.setProject(dataItem.getProject());
            dataItemMetadata.setEmbedded(dataItem.getEmbedded());
            dataItemMetadata.setCreated(dataItem.getCreated());
            dataItemMetadata.setUpdated(dataItem.getUpdated());
            dto.setMetadata(dataItemMetadata);
          })
          .withIfElse(
            embeddable,
            (dto, condition) ->
              Optional
                .ofNullable(dataItem.getEmbedded())
                .filter(embedded -> !condition || embedded)
                .ifPresent(embedded ->
                  dto.setSpec(
                    ConversionUtils.reverse(dataItem.getSpec(), "cbor")
                  )
                )
          )
          .withIfElse(
            embeddable,
            (dto, condition) ->
              Optional
                .ofNullable(dataItem.getEmbedded())
                .filter(embedded -> !condition || embedded)
                .ifPresent(embedded ->
                  dto.setExtra(
                    ConversionUtils.reverse(dataItem.getExtra(), "cbor")
                  )
                )
          )
          .withIfElse(
            embeddable,
            (dto, condition) ->
              Optional
                .ofNullable(dataItem.getEmbedded())
                .filter(embedded -> !condition || embedded)
                .ifPresent(embedded ->
                  dto.setStatus(
                    MapUtils.mergeMultipleMaps(
                      ConversionUtils.reverse(dataItem.getStatus(), "cbor"),
                      Map.of("state", dataItem.getState())
                    )
                  )
                )
          )
    );
  }
}
