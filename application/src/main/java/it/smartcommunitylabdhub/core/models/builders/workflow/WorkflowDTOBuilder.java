package it.smartcommunitylabdhub.core.models.builders.workflow;

import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.commons.models.metadata.EmbeddableMetadata;
import it.smartcommunitylabdhub.commons.utils.MapUtils;
import it.smartcommunitylabdhub.core.models.entities.WorkflowEntity;
import it.smartcommunitylabdhub.core.models.metadata.AuditMetadataBuilder;
import it.smartcommunitylabdhub.core.models.metadata.BaseMetadataBuilder;
import it.smartcommunitylabdhub.core.models.metadata.VersioningMetadataBuilder;
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
public class WorkflowDTOBuilder implements Converter<WorkflowEntity, Workflow> {

    private final AttributeConverter<Map<String, Serializable>, byte[]> converter;
    private BaseMetadataBuilder baseMetadataBuilder;
    private AuditMetadataBuilder auditingMetadataBuilder;
    private VersioningMetadataBuilder versioningMetadataBuilder;

    public WorkflowDTOBuilder(
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

    @Autowired
    public void setVersioningMetadataBuilder(VersioningMetadataBuilder versioningMetadataBuilder) {
        this.versioningMetadataBuilder = versioningMetadataBuilder;
    }

    public Workflow build(WorkflowEntity entity) {
        //read metadata map as-is
        Map<String, Serializable> meta = converter.convertToEntityAttribute(entity.getMetadata());

        // build metadata
        Map<String, Serializable> metadata = new HashMap<>();
        metadata.putAll(meta);

        EmbeddableMetadata embeddable = EmbeddableMetadata.from(meta);
        embeddable.setEmbedded(entity.getEmbedded());
        metadata.putAll(embeddable.toMap());

        Optional.of(baseMetadataBuilder.convert(entity)).ifPresent(m -> metadata.putAll(m.toMap()));
        Optional.of(auditingMetadataBuilder.convert(entity)).ifPresent(m -> metadata.putAll(m.toMap()));
        Optional.of(versioningMetadataBuilder.convert(entity)).ifPresent(m -> metadata.putAll(m.toMap()));

        return Workflow
            .builder()
            .id(entity.getId())
            .name(entity.getName())
            .kind(entity.getKind())
            .project(entity.getProject())
            .user(entity.getCreatedBy())
            .metadata(metadata)
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
    public Workflow convert(WorkflowEntity source) {
        return build(source);
    }
}
