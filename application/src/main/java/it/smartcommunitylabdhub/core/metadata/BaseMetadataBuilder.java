package it.smartcommunitylabdhub.core.metadata;

import it.smartcommunitylabdhub.commons.models.metadata.BaseMetadata;
import it.smartcommunitylabdhub.core.persistence.BaseEntity;
import jakarta.persistence.AttributeConverter;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Component
public class BaseMetadataBuilder implements Converter<BaseEntity, BaseMetadata> {

    private final AttributeConverter<Map<String, Serializable>, byte[]> converter;

    public BaseMetadataBuilder(
        @Qualifier("cborMapConverter") AttributeConverter<Map<String, Serializable>, byte[]> cborConverter
    ) {
        this.converter = cborConverter;
    }

    @Override
    public BaseMetadata convert(BaseEntity entity) {
        Assert.notNull(entity, "entity can not be null");
        //read metadata map as-is
        Map<String, Serializable> meta = converter.convertToEntityAttribute(entity.getMetadata());

        BaseMetadata metadata = BaseMetadata.from(meta);

        //inflate with values from entity
        metadata.setProject(entity.getProject());

        if (!StringUtils.hasText(metadata.getName())) {
            metadata.setName(entity.getName());
        }

        metadata.setCreated(
            entity.getCreated() != null
                ? OffsetDateTime.ofInstant(entity.getCreated().toInstant(), ZoneOffset.UTC)
                : null
        );
        metadata.setUpdated(
            entity.getUpdated() != null
                ? OffsetDateTime.ofInstant(entity.getUpdated().toInstant(), ZoneOffset.UTC)
                : null
        );

        return metadata;
    }
}
