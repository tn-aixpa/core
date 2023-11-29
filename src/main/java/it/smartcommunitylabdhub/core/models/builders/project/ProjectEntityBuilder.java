package it.smartcommunitylabdhub.core.models.builders.project;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.project.Project;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import it.smartcommunitylabdhub.core.models.entities.project.specs.ProjectBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.project.specs.ProjectProjectSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProjectEntityBuilder {

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    /**
     * Build a project from a projectDTO and store extra values as a cbor
     *
     * @param projectDTO the Project DTO To convert
     * @return Project
     */
    public ProjectEntity build(Project projectDTO) {

        specRegistry.createSpec(projectDTO.getKind(), EntityName.PROJECT, Map.of());

        // Retrieve object spec
        ProjectBaseSpec<?> projectSpec = JacksonMapper.objectMapper
                .convertValue(
                        projectDTO.getSpec(),
                        ProjectBaseSpec.class
                );
        Map<String, Object> spec = projectSpec.toMap();

        return EntityFactory.combine(
                ConversionUtils.convert(projectDTO, "project"), projectDTO,
                builder -> builder
                        .with(p -> p.setExtra(
                                ConversionUtils.convert(projectDTO
                                                .getExtra(),
                                        "cbor")))
                        .with(p -> {
                            spec.remove("functions");
                            spec.remove("workflows");
                            spec.remove("artifacts");
                            spec.remove("dataitems");
                            p.setSpec(ConversionUtils.convert(spec, "cbor"));
                        })
                        .with(p -> p.setMetadata(
                                ConversionUtils.convert(projectDTO
                                                .getMetadata(),
                                        "metadata")))

        );
    }

    /**
     * Update a project if element is not passed it override causing empty field
     *
     * @param project    entity
     * @param projectDTO the DTO to combine with the project entity
     * @return Project
     */
    public ProjectEntity update(ProjectEntity project, Project projectDTO) {

        // Retrieve object spec
        ProjectBaseSpec<?> projectSpec = JacksonMapper.objectMapper
                .convertValue(
                        projectDTO.getSpec(),
                        ProjectProjectSpec.class
                );
        Map<String, Object> spec = projectSpec.toMap();

        return EntityFactory.combine(
                project, projectDTO, builder -> builder
                        .with(p -> p.setDescription(
                                projectDTO.getDescription()))
                        .with(p -> p.setSource(projectDTO.getMetadata().getSource()))
                        .with(p -> p.setState(projectDTO.getState() == null
                                ? State.CREATED
                                : State.valueOf(projectDTO
                                .getState())))
                        .with(p -> p.setExtra(
                                ConversionUtils.convert(projectDTO
                                                .getExtra(),
                                        "cbor")))
                        .with(p -> {
                            spec.remove("functions");
                            spec.remove("workflows");
                            spec.remove("artifacts");
                            spec.remove("dataitems");
                            p.setSpec(ConversionUtils.convert(spec, "cbor"));
                        })
                        .with(p -> p.setMetadata(
                                ConversionUtils.convert(projectDTO
                                                .getMetadata(),
                                        "metadata"))));
    }
}
