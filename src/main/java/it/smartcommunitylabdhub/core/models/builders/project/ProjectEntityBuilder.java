package it.smartcommunitylabdhub.core.models.builders.project;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.Accessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.ProjectFieldAccessor;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.project.Project;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import it.smartcommunitylabdhub.core.models.entities.project.specs.ProjectBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.project.specs.ProjectProjectSpec;
import it.smartcommunitylabdhub.core.models.enums.State;
import it.smartcommunitylabdhub.core.utils.JacksonMapper;
import it.smartcommunitylabdhub.core.utils.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProjectEntityBuilder {

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;


    /**
     * Build a project from a projectDTO and store extra values as a cbor
     * <p>
     *
     * @param projectDTO the Project DTO To convert
     * @return Project
     */
    public ProjectEntity build(Project projectDTO) {

        // Validate Spec
        specRegistry.createSpec(projectDTO.getKind(), EntityName.PROJECT, Map.of());

        // Retrieve field accessor
        ProjectFieldAccessor<?> projectFieldAccessor =
                accessorRegistry.createAccessor(
                        projectDTO.getKind(),
                        EntityName.PROJECT,
                        JacksonMapper.objectMapper.convertValue(projectDTO,
                                JacksonMapper.typeRef));

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
                        .withIfElse(projectFieldAccessor.getState().equals(State.NONE.name()),
                                (dto, condition) -> {
                                    if (condition) {
                                        dto.setStatus(ConversionUtils.convert(
                                                MapUtils.mergeMultipleMaps(
                                                        projectFieldAccessor.getStatus(),
                                                        Map.of("state", State.CREATED.name())
                                                ), "cbor")
                                        );
                                        dto.setState(State.CREATED);
                                    } else {
                                        dto.setStatus(
                                                ConversionUtils.convert(
                                                        projectFieldAccessor.getStatus(),
                                                        "cbor")
                                        );
                                        dto.setState(State.valueOf(projectFieldAccessor.getState()));
                                    }
                                }
                        )
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

        // Validate Spec
        specRegistry.createSpec(projectDTO.getKind(), EntityName.PROJECT, Map.of());
        
        // Retrieve field accessor
        ProjectFieldAccessor<?> projectFieldAccessor =
                accessorRegistry.createAccessor(
                        projectDTO.getKind(),
                        EntityName.PROJECT,
                        JacksonMapper.objectMapper.convertValue(projectDTO,
                                JacksonMapper.typeRef));

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
                        .withIfElse(projectFieldAccessor.getState().equals(State.NONE.name()),
                                (dto, condition) -> {
                                    if (condition) {
                                        dto.setStatus(ConversionUtils.convert(
                                                MapUtils.mergeMultipleMaps(
                                                        projectFieldAccessor.getStatus(),
                                                        Map.of("state", State.CREATED.name())
                                                ), "cbor")
                                        );
                                        dto.setState(State.CREATED);
                                    } else {
                                        dto.setStatus(
                                                ConversionUtils.convert(
                                                        projectFieldAccessor.getStatus(),
                                                        "cbor")
                                        );
                                        dto.setState(State.valueOf(projectFieldAccessor.getState()));
                                    }
                                }
                        )
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
