package it.smartcommunitylabdhub.core.models.builders.project;

import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.entities.project.ProjectBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.project.ProjectMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import it.smartcommunitylabdhub.core.models.specs.project.ProjectSpec;
import jakarta.persistence.AttributeConverter;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ProjectDTOBuilder implements Converter<ProjectEntity, Project> {

    private final AttributeConverter<Map<String, Serializable>, byte[]> converter;

    public ProjectDTOBuilder(
        @Qualifier("cborMapConverter") AttributeConverter<Map<String, Serializable>, byte[]> cborConverter
    ) {
        this.converter = cborConverter;
    }

    public Project build(ProjectEntity entity) {
        //read metadata map as-is
        Map<String, Serializable> meta = converter.convertToEntityAttribute(entity.getMetadata());

        // build metadata
        ProjectMetadata metadata = new ProjectMetadata();
        metadata.configure(meta);

        if (!StringUtils.hasText(metadata.getName())) {
            metadata.setName(entity.getName());
        }
        metadata.setProject(entity.getProject());
        metadata.setSource(entity.getSource());
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

        //transform base spec to full spec
        ProjectBaseSpec baseSpec = new ProjectBaseSpec();
        baseSpec.configure(converter.convertToEntityAttribute(entity.getSpec()));
        ProjectSpec spec = new ProjectSpec();
        spec.configure(baseSpec.toMap());

        return Project
            .builder()
            .id(entity.getId())
            .name(entity.getName())
            .metadata(MapUtils.mergeMultipleMaps(meta, metadata.toMap()))
            .spec(converter.convertToEntityAttribute(entity.getSpec()))
            .extra(converter.convertToEntityAttribute(entity.getExtra()))
            .status(
                MapUtils.mergeMultipleMaps(
                    converter.convertToEntityAttribute(entity.getStatus()),
                    Map.of("state", entity.getState().toString())
                )
            )
            .build();
    }

    @Override
    public Project convert(ProjectEntity source) {
        return build(source);
    }
}
