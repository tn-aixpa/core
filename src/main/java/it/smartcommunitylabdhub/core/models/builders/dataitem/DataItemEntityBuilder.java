package it.smartcommunitylabdhub.core.models.builders.dataitem;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.DataItemFieldAccessor;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import it.smartcommunitylabdhub.core.models.entities.dataitem.specs.DataItemBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import it.smartcommunitylabdhub.core.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataItemEntityBuilder {

    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;

    /**
     * Build d dataItem from d dataItemDTO and store extra values as d cbor
     * <p>
     * specRegistry can be also used
     * <p>
     * Autowired
     * SpecRegistry<? extends Spec> specRegistry;
     * specRegistry.createSpec(dataItemDTO.getKind(), EntityName.DATAITEM, Map.of());
     *
     * @return DataItemDTO
     */
    public DataItemEntity build(DataItem dataItemDTO) {

        // Retrieve field accessor
        DataItemFieldAccessor<?> dataItemFieldAccessor =
                accessorRegistry.createAccessor(
                        "dataitem",
                        EntityName.DATAITEM,
                        JacksonMapper.objectMapper.convertValue(dataItemDTO,
                                JacksonMapper.typeRef));


        // Retrieve Spec
        DataItemBaseSpec<?> spec = JacksonMapper.objectMapper
                .convertValue(dataItemDTO.getSpec(), DataItemBaseSpec.class);
        return EntityFactory.combine(
                ConversionUtils.convert(dataItemDTO, "dataitem"), dataItemDTO,
                builder -> builder
                        .withIfElse(dataItemFieldAccessor.getState().equals(State.NONE.name()),
                                (dto, condition) -> {
                                    if (condition) {
                                        dto.setStatus(ConversionUtils.convert(
                                                MapUtils.mergeMultipleMaps(
                                                        dataItemFieldAccessor.getStatus(),
                                                        Map.of("state", State.CREATED.name())
                                                ), "cbor")
                                        );
                                        dto.setState(State.CREATED);
                                    } else {
                                        dto.setStatus(
                                                ConversionUtils.convert(
                                                        dataItemFieldAccessor.getStatus(),
                                                        "cbor")
                                        );
                                    }
                                }
                        )
                        .with(d -> d.setMetadata(
                                ConversionUtils.convert(dataItemDTO
                                                .getMetadata(),
                                        "metadata")))
                        .with(d -> d.setExtra(
                                ConversionUtils.convert(dataItemDTO
                                                .getExtra(),
                                        "cbor")))
                        .with(d -> d.setSpec(ConversionUtils.convert(spec.toMap(), "cbor"))));

    }

    /**
     * Update d dataItem if element is not passed it override causing empty field
     *
     * @param dataItem    the Dataitem
     * @param dataItemDTO the Dataitem DTO to combine
     * @return Dataitem
     */
    public DataItemEntity update(DataItemEntity dataItem, DataItem dataItemDTO) {

        // Retrieve field accessor
        DataItemFieldAccessor<?> dataItemFieldAccessor =
                accessorRegistry.createAccessor(
                        "dataitem",
                        EntityName.DATAITEM,
                        JacksonMapper.objectMapper.convertValue(dataItemDTO,
                                JacksonMapper.typeRef));

        return EntityFactory.combine(
                dataItem, dataItemDTO, builder -> builder
                        .withIfElse(dataItemFieldAccessor.getState().equals(State.NONE.name()),
                                (dto, condition) -> {
                                    if (condition) {
                                        dto.setStatus(ConversionUtils.convert(
                                                MapUtils.mergeMultipleMaps(
                                                        dataItemFieldAccessor.getStatus(),
                                                        Map.of("state", State.CREATED.name())
                                                ), "cbor")
                                        );
                                        dto.setState(State.CREATED);
                                    } else {
                                        dto.setStatus(
                                                ConversionUtils.convert(
                                                        dataItemFieldAccessor.getStatus(),
                                                        "cbor")
                                        );
                                    }
                                }
                        )
                        .with(d -> d.setMetadata(
                                ConversionUtils.convert(dataItemDTO
                                                .getMetadata(),
                                        "metadata")))
                        .with(d -> d.setExtra(
                                ConversionUtils.convert(dataItemDTO
                                                .getExtra(),

                                        "cbor")))
                        .with(d -> d.setEmbedded(
                                dataItemDTO.getEmbedded())));
    }
}
