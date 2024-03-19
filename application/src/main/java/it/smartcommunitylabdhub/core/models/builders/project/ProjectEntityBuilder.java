package it.smartcommunitylabdhub.core.models.builders.project;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.entities.project.ProjectBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.project.ProjectMetadata;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import java.time.ZoneOffset;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProjectEntityBuilder implements Converter<Project, ProjectEntity> {

    private final CBORConverter cborConverter;

    public ProjectEntityBuilder(CBORConverter cborConverter) {
        this.cborConverter = cborConverter;
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
            .metadata(cborConverter.convert(dto.getMetadata()))
            .spec(cborConverter.convert(spec.toMap()))
            .status(cborConverter.convert(dto.getStatus()))
            .extra(cborConverter.convert(dto.getExtra()))
            .state(
                // Store status if not present
                statusFieldAccessor.getState() == null ? State.CREATED : State.valueOf(statusFieldAccessor.getState())
            )
            // Metadata Extraction
            .source(metadata.getSource())
            .created(
                metadata.getCreated() != null
                    ? Date.from(metadata.getCreated().atZone(ZoneOffset.UTC).toInstant())
                    : null
            )
            .updated(
                metadata.getUpdated() != null
                    ? Date.from(metadata.getUpdated().atZone(ZoneOffset.UTC).toInstant())
                    : null
            )
            .build();
    }

    @Override
    public ProjectEntity convert(Project source) {
        return build(source);
    }
}
