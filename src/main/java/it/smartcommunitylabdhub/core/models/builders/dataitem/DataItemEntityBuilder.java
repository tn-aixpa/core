package it.smartcommunitylabdhub.core.models.builders.dataitem;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.DataItemFieldAccessor;
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

    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;

    /**
     * Build d dataItem from d dataItemDTO and store extra values as d cbor
     * <p>
     *
     * @return DataItemDTO
     */
    public DataItemEntity build(DataItem dataItemDTO) {

        // Validate Spec
        specRegistry.createSpec(dataItemDTO.getKind(), EntityName.DATAITEM, Map.of());

        // Retrieve field accessor
        DataItemFieldAccessor<?> dataItemFieldAccessor =
                accessorRegistry.createAccessor(
                        dataItemDTO.getKind(),
                        EntityName.DATAITEM,
                        JacksonMapper.objectMapper.convertValue(dataItemDTO,
                                JacksonMapper.typeRef));


        // Retrieve Spec
        DataItemBaseSpec<?> spec = JacksonMapper.objectMapper
                .convertValue(dataItemDTO.getSpec(), DataItemBaseSpec.class);

        return EntityFactory.combine(
                ConversionUtils.convert(dataItemDTO, "dataitem"), dataItemDTO,
                builder -> builder
                        .with(p -> p.setMetadata(ConversionUtils.convert(
                                dataItemDTO.getMetadata(), "metadata")))
                        .with(a -> a.setExtra(ConversionUtils.convert(
                                dataItemDTO.getExtra(), "cbor")))
                        .with(a -> a.setSpec(ConversionUtils.convert(
                                spec.toMap(), "cbor")))

                        // Store status if not present
                        .withIfElse(dataItemFieldAccessor.getState().equals(State.NONE.name()),
                                (a, condition) -> {
                                    if (condition) {
                                        a.setState(State.CREATED);
                                    } else {
                                        a.setState(State.valueOf(dataItemFieldAccessor.getState()));
                                    }
                                }
                        )

                        // Metadata Extraction
                        .withIfElse(dataItemDTO.getMetadata().getEmbedded() == null,
                                (a, condition) -> {
                                    if (condition) {
                                        a.setEmbedded(false);
                                    } else {
                                        a.setEmbedded(dataItemDTO.getMetadata().getEmbedded());
                                    }
                                }
                        )
                        .withIf(dataItemDTO.getMetadata().getCreated() != null, (a) ->
                                a.setCreated(dataItemDTO.getMetadata().getCreated()))
                        .withIf(dataItemDTO.getMetadata().getUpdated() != null, (a) ->
                                a.setUpdated(dataItemDTO.getMetadata().getUpdated())
                        )
        );

    }

    /**
     * Update d dataItem if element is not passed it override causing empty field
     *
     * @param dataItem    the Dataitem
     * @param dataItemDTO the Dataitem DTO to combine
     * @return Dataitem
     */
    public DataItemEntity update(DataItemEntity dataItem, DataItem dataItemDTO) {

        // Validate Spec
        specRegistry.createSpec(dataItemDTO.getKind(), EntityName.DATAITEM, Map.of());

        // Retrieve field accessor
        DataItemFieldAccessor<?> dataItemFieldAccessor =
                accessorRegistry.createAccessor(
                        dataItemDTO.getKind(),
                        EntityName.DATAITEM,
                        JacksonMapper.objectMapper.convertValue(dataItemDTO,
                                JacksonMapper.typeRef));

        return EntityFactory.combine(
                dataItem, dataItemDTO, builder -> builder
                        .withIfElse(dataItemFieldAccessor.getState().equals(State.NONE.name()),
                                (a, condition) -> {
                                    if (condition) {
                                        a.setState(State.CREATED);
                                    } else {
                                        a.setState(State.valueOf(dataItemFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(a -> a.setMetadata(ConversionUtils.convert(dataItemDTO
                                .getMetadata(), "metadata")))
                        .with(a -> a.setExtra(ConversionUtils.convert(dataItemDTO
                                .getExtra(), "cbor")))

                        // Metadata Extraction
                        .withIfElse(dataItemDTO.getMetadata().getEmbedded() == null,
                                (a, condition) -> {
                                    if (condition) {
                                        a.setEmbedded(false);
                                    } else {
                                        a.setEmbedded(dataItemDTO.getMetadata().getEmbedded());
                                    }
                                }
                        )
        );
    }
}
