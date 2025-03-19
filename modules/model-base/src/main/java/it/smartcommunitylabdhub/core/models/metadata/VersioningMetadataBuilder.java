package it.smartcommunitylabdhub.core.models.metadata;

import it.smartcommunitylabdhub.commons.models.metadata.VersioningMetadata;
import it.smartcommunitylabdhub.core.models.base.BaseEntity;
import jakarta.persistence.AttributeConverter;
import java.io.Serializable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Component
public class VersioningMetadataBuilder implements Converter<BaseEntity, VersioningMetadata> {

    private final AttributeConverter<Map<String, Serializable>, byte[]> converter;

    public VersioningMetadataBuilder(
        @Qualifier("cborMapConverter") AttributeConverter<Map<String, Serializable>, byte[]> cborConverter
    ) {
        this.converter = cborConverter;
    }

    @Override
    public VersioningMetadata convert(@NonNull BaseEntity entity) {
        Assert.notNull(entity, "entity can not be null");
        //read metadata map as-is
        Map<String, Serializable> meta = converter.convertToEntityAttribute(entity.getMetadata());

        VersioningMetadata metadata = VersioningMetadata.from(meta);

        //inflate with values from entity
        metadata.setProject(entity.getProject());

        if (!StringUtils.hasText(metadata.getName())) {
            metadata.setName(entity.getName());
        }

        if (!StringUtils.hasText(metadata.getVersion())) {
            metadata.setVersion(entity.getId());
        }

        return metadata;
    }
}
