package it.smartcommunitylabdhub.core.models.builders.project;

import it.smartcommunitylabdhub.commons.models.project.Project;
import it.smartcommunitylabdhub.commons.models.project.ProjectBaseSpec;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.entities.ProjectEntity;
import it.smartcommunitylabdhub.core.models.metadata.AuditMetadataBuilder;
import it.smartcommunitylabdhub.core.models.metadata.BaseMetadataBuilder;
import it.smartcommunitylabdhub.core.models.specs.project.ProjectSpec;
import jakarta.persistence.AttributeConverter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProjectDTOBuilder implements Converter<ProjectEntity, Project> {

    private final AttributeConverter<Map<String, Serializable>, byte[]> converter;
    private BaseMetadataBuilder baseMetadataBuilder;
    private AuditMetadataBuilder auditingMetadataBuilder;

    public ProjectDTOBuilder(
        @Qualifier("cborMapConverter") AttributeConverter<Map<String, Serializable>, byte[]> cborConverter
    ) {
        this.converter = cborConverter;
    }

    @Autowired
    public void setBaseMetadataBuilder(BaseMetadataBuilder baseMetadataBuilder) {
        this.baseMetadataBuilder = baseMetadataBuilder;
    }

    @Autowired
    public void setAuditingMetadataBuilder(AuditMetadataBuilder auditingMetadataBuilder) {
        this.auditingMetadataBuilder = auditingMetadataBuilder;
    }

    public Project build(ProjectEntity entity) {
        //read metadata map as-is
        Map<String, Serializable> meta = converter.convertToEntityAttribute(entity.getMetadata());

        // build metadata
        Map<String, Serializable> metadata = new HashMap<>();
        metadata.putAll(meta);

        Optional.of(baseMetadataBuilder.convert(entity)).ifPresent(m -> metadata.putAll(m.toMap()));
        Optional.of(auditingMetadataBuilder.convert(entity)).ifPresent(m -> metadata.putAll(m.toMap()));

        //transform base spec to full spec
        ProjectBaseSpec baseSpec = new ProjectBaseSpec();
        baseSpec.configure(converter.convertToEntityAttribute(entity.getSpec()));
        ProjectSpec spec = new ProjectSpec();
        spec.configure(baseSpec.toMap());

        return Project
            .builder()
            .id(entity.getId())
            .name(entity.getName())
            .user(entity.getCreatedBy())
            .metadata(metadata)
            .spec(converter.convertToEntityAttribute(entity.getSpec()))
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
