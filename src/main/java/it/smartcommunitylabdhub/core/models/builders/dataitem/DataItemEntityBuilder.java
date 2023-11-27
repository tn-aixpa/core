package it.smartcommunitylabdhub.core.models.builders.dataitem;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecEntity;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import it.smartcommunitylabdhub.core.models.entities.dataitem.specs.DataItemBaseSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataItemEntityBuilder {

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    /**
     * Build d dataItem from d dataItemDTO and store extra values as d cbor
     *
     * @return DataItemDTO
     */
    public DataItemEntity build(DataItem dataItemDTO) {

        specRegistry.createSpec(dataItemDTO.getKind(), SpecEntity.DATAITEM, Map.of());

        // Retrieve Spec
        DataItemBaseSpec<?> spec = JacksonMapper.objectMapper
                .convertValue(dataItemDTO.getSpec(), DataItemBaseSpec.class);
        return EntityFactory.combine(
                ConversionUtils.convert(dataItemDTO, "dataitem"), dataItemDTO,
                builder -> builder
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

        // Retrieve object spec
        // Retrieve Spec
        DataItemBaseSpec spec = JacksonMapper.objectMapper
                .convertValue(dataItemDTO.getSpec(), DataItemBaseSpec.class);


        return EntityFactory.combine(
                dataItem, dataItemDTO, builder -> builder
                        .with(d -> d.setState(dataItemDTO.getState() == null
                                ? State.CREATED
                                : State.valueOf(dataItemDTO
                                .getState())))

                        .with(d -> d.setMetadata(
                                ConversionUtils.convert(dataItemDTO
                                                .getMetadata(),
                                        "metadata")))
                        .with(d -> d.setExtra(
                                ConversionUtils.convert(dataItemDTO
                                                .getExtra(),

                                        "cbor")))
                        .with(d -> d.setSpec(ConversionUtils.convert(spec.toMap(), "cbor")))
                        .with(d -> d.setEmbedded(
                                dataItemDTO.getEmbedded())));
    }
}
