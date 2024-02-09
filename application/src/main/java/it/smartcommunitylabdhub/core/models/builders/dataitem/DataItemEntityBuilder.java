package it.smartcommunitylabdhub.core.models.builders.dataitem;

import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.commons.models.accessors.fields.DataItemFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.specs.DataItemBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataItemEntityBuilder {

  @Autowired
  SpecRegistry specRegistry;

  /**
   * Build a dataItem from a dataItemDTO and store extra values as d cbor
   * <p>
   *
   * @return DataItemDTO
   */
  public DataItemEntity build(DataItem dataItemDTO) {
    // Validate Spec
    specRegistry.createSpec(
      dataItemDTO.getKind(),
      EntityName.DATAITEM,
      Map.of()
    );

    // Retrieve field accessor
    DataItemFieldAccessor dataItemFieldAccessor = DataItemFieldAccessor.with(
      JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
        dataItemDTO,
        JacksonMapper.typeRef
      )
    );

    // Retrieve Spec
    DataItemBaseSpec spec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
      dataItemDTO.getSpec(),
      DataItemBaseSpec.class
    );

    return EntityFactory.combine(
      DataItemEntity.builder().build(),
      dataItemDTO,
      builder ->
        builder
          .withIf(
            dataItemDTO.getId() != null,
            d -> d.setId(dataItemDTO.getId())
          )
          .with(d -> d.setName(dataItemDTO.getName()))
          .with(d -> d.setKind(dataItemDTO.getKind()))
          .with(d -> d.setProject(dataItemDTO.getProject()))
          .with(d ->
            d.setMetadata(
              ConversionUtils.convert(dataItemDTO.getMetadata(), "metadata")
            )
          )
          .with(d ->
            d.setExtra(ConversionUtils.convert(dataItemDTO.getExtra(), "cbor"))
          )
          .with(d -> d.setSpec(ConversionUtils.convert(spec.toMap(), "cbor")))
          .with(d ->
            d.setStatus(
              ConversionUtils.convert(dataItemDTO.getStatus(), "cbor")
            )
          )
          // Store status if not present
          .withIfElse(
            dataItemFieldAccessor.getState().equals(State.NONE.name()),
            (d, condition) -> {
              if (condition) {
                d.setState(State.CREATED);
              } else {
                d.setState(State.valueOf(dataItemFieldAccessor.getState()));
              }
            }
          )
          // Metadata Extraction
          .withIfElse(
            dataItemDTO.getMetadata().getEmbedded() == null,
            (d, condition) -> {
              if (condition) {
                d.setEmbedded(false);
              } else {
                d.setEmbedded(dataItemDTO.getMetadata().getEmbedded());
              }
            }
          )
          .withIf(
            dataItemDTO.getMetadata().getCreated() != null,
            d -> d.setCreated(dataItemDTO.getMetadata().getCreated())
          )
          .withIf(
            dataItemDTO.getMetadata().getUpdated() != null,
            d -> d.setUpdated(dataItemDTO.getMetadata().getUpdated())
          )
    );
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

  private DataItemEntity doUpdate(
    DataItemEntity dataItem,
    DataItemEntity newDataItem
  ) {
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
          .with(d -> d.setMetadata(newDataItem.getMetadata()))
          .with(d -> d.setExtra(newDataItem.getExtra()))
          .with(d -> d.setStatus(newDataItem.getStatus()))
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
