package it.smartcommunitylabdhub.core.models.builders.dataitem;

import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItemMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import java.io.Serializable;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DataItemDTOBuilder implements Converter<DataItemEntity, DataItem> {

    private final CBORConverter cborConverter;

    public DataItemDTOBuilder(CBORConverter cborConverter) {
        this.cborConverter = cborConverter;
    }

    public DataItem build(DataItemEntity entity, boolean embeddable) {
        //read metadata map as-is
        Map<String, Serializable> meta = cborConverter.reverseConvert(entity.getMetadata());

        // build metadata
        DataItemMetadata metadata = new DataItemMetadata();
        metadata.configure(meta);

        if (!StringUtils.hasText(metadata.getVersion())) {
            metadata.setVersion(entity.getId());
        }
        if (!StringUtils.hasText(metadata.getName())) {
            metadata.setName(entity.getName());
        }
        metadata.setProject(entity.getProject());
        metadata.setEmbedded(entity.getEmbedded());
        metadata.setCreated(entity.getCreated());
        metadata.setUpdated(entity.getUpdated());

        return DataItem
            .builder()
            .id(entity.getId())
            .name(entity.getName())
            .kind(entity.getKind())
            .project(entity.getProject())
            .metadata(MapUtils.mergeMultipleMaps(meta, metadata.toMap()))
            .spec(embeddable ? null : cborConverter.reverseConvert(entity.getSpec()))
            .extra(embeddable ? null : cborConverter.reverseConvert(entity.getExtra()))
            .status(
                embeddable
                    ? null
                    : MapUtils.mergeMultipleMaps(
                        cborConverter.reverseConvert(entity.getStatus()),
                        Map.of("state", entity.getState().toString())
                    )
            )
            .build();
    }

    @Override
    public DataItem convert(DataItemEntity source) {
        return build(source, false);
    }
}
