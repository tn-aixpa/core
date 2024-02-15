package it.smartcommunitylabdhub.core.models.builders.project;

import it.smartcommunitylabdhub.commons.accessors.fields.StatusFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.entities.project.ProjectMetadata;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.services.SpecRegistry;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.types.CBORConverter;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import java.io.Serializable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProjectEntityBuilder implements Converter<Project, ProjectEntity> {

    @Autowired
    CBORConverter cborConverter;

    @Autowired
    SpecRegistry specRegistry;

    /**
     * Build a project from a projectDTO and store extra values as p cbor
     * <p>
     *
     * @param dto the Project DTO To convert
     * @return Project
     */
    public ProjectEntity build(Project dto) {
        // Parse and export Spec
        Map<String, Serializable> spec = specRegistry
            .createSpec(dto.getKind(), EntityName.PROJECT, dto.getSpec())
            .toMap();

        // Retrieve field accessor
        StatusFieldAccessor statusFieldAccessor = StatusFieldAccessor.with(dto.getStatus());
        ProjectMetadata metadata = new ProjectMetadata();
        metadata.configure(dto.getMetadata());

        return EntityFactory.combine(
            ProjectEntity.builder().build(),
            dto,
            builder ->
                builder
                    // check id
                    .withIf(dto.getId() != null, e -> e.setId(dto.getId()))
                    .with(e -> e.setName(dto.getName()))
                    .with(e -> e.setKind(dto.getKind()))
                    .withIfElse(
                        (statusFieldAccessor.getState() == null),
                        (p, condition) -> {
                            if (condition) {
                                p.setState(State.CREATED);
                            } else {
                                p.setState(State.valueOf(statusFieldAccessor.getState()));
                            }
                        }
                    )
                    .with(e -> e.setMetadata(cborConverter.convert(dto.getMetadata())))
                    .with(e -> e.setStatus(cborConverter.convert(dto.getStatus())))
                    .with(e -> e.setExtra(cborConverter.convert(dto.getExtra())))
                    .with(e -> {
                        spec.remove("functions");
                        spec.remove("artifacts");
                        spec.remove("workflows");
                        spec.remove("dataitems");
                        e.setSpec(cborConverter.convert(spec));
                    })
                    // Metadata Extraction
                    .withIf(metadata.getSource() != null, e -> e.setSource(metadata.getSource()))
                    .withIf(metadata.getDescription() != null, e -> e.setDescription(metadata.getDescription()))
                    .withIf(metadata.getCreated() != null, e -> e.setCreated(metadata.getCreated()))
                    .withIf(metadata.getUpdated() != null, e -> e.setUpdated(metadata.getUpdated()))
        );
    }

    @Override
    public ProjectEntity convert(Project source) {
        return build(source);
    }

    /**
     * Update a project if element is not passed it override causing empty field
     *
     * @param project    entity
     * @param projectDTO the DTO to combine with the project entity
     * @return Project
     */
    public ProjectEntity update(ProjectEntity project, Project projectDTO) {
        ProjectEntity newProject = build(projectDTO);
        return doUpdate(project, newProject);
    }

    private ProjectEntity doUpdate(ProjectEntity project, ProjectEntity newProject) {
        return EntityFactory.combine(
            project,
            newProject,
            builder ->
                builder
                    .withIfElse(
                        newProject.getState().name().equals(State.NONE.name()),
                        (p, condition) -> {
                            if (condition) {
                                p.setState(State.CREATED);
                            } else {
                                p.setState(newProject.getState());
                            }
                        }
                    )
                    .with(e -> e.setMetadata(newProject.getMetadata()))
                    .with(e -> e.setExtra(newProject.getExtra()))
                    .with(e -> e.setStatus(newProject.getStatus()))
                    .with(e -> e.setSpec(newProject.getSpec()))
                    .withIf(newProject.getSource() != null, e -> e.setSource(newProject.getSource()))
                    .withIf(newProject.getDescription() != null, e -> e.setDescription(newProject.getDescription()))
        );
    }
}
