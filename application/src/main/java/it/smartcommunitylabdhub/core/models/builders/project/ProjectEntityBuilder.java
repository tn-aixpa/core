package it.smartcommunitylabdhub.core.models.builders.project;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.entities.project.ProjectBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.project.ProjectMetadata;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import jakarta.persistence.AttributeConverter;
import java.io.Serializable;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProjectEntityBuilder implements Converter<Project, ProjectEntity> {

    private final AttributeConverter<Map<String, Serializable>, byte[]> converter;

    public ProjectEntityBuilder(
        @Qualifier("cborMapConverter") AttributeConverter<Map<String, Serializable>, byte[]> cborConverter
    ) {
        this.converter = cborConverter;
    }

    /**
     * Build a project from a projectDTO and store extra values as p cbor
     * <p>
     *
     * @param dto the Project DTO To convert
     * @return Project
     */
    public ProjectEntity build(Project dto) {
        // Retrieve field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        ProjectMetadata metadata = new ProjectMetadata();
        metadata.configure(dto.getMetadata());

        //store only base project spec
        //deletes embedded content
        ProjectBaseSpec spec = new ProjectBaseSpec();
        spec.configure(dto.getSpec());

        return ProjectEntity
            .builder()
            .id(dto.getId())
            .name(dto.getName())
            .kind(dto.getKind())
            .metadata(converter.convertToDatabaseColumn(dto.getMetadata()))
            .spec(converter.convertToDatabaseColumn(spec.toMap()))
            .status(converter.convertToDatabaseColumn(dto.getStatus()))
            .extra(converter.convertToDatabaseColumn(dto.getExtra()))
            .state(
                // Store status if not present
                statusFieldAccessor.getState() == null ? State.CREATED : State.valueOf(statusFieldAccessor.getState())
            )
            // Metadata Extraction
            .source(metadata.getSource())
            .created(
                metadata.getCreated() != null
                    ? Date.from(metadata.getCreated().atZoneSameInstant(ZoneOffset.UTC).toInstant())
                    : null
            )
            .updated(
                metadata.getUpdated() != null
                    ? Date.from(metadata.getUpdated().atZoneSameInstant(ZoneOffset.UTC).toInstant())
                    : null
            )
            .build();
    }

    @Override
    public ProjectEntity convert(Project source) {
        return build(source);
    }
}
