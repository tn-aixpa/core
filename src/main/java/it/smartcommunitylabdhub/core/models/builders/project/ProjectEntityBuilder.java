package it.smartcommunitylabdhub.core.models.builders.project;

import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorRegistry;
import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
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
import it.smartcommunitylabdhub.core.utils.ErrorList;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class ProjectEntityBuilder {

    @Autowired
    SpecRegistry<? extends Spec> specRegistry;

    @Autowired
    AccessorRegistry<? extends Accessor<Object>> accessorRegistry;


    /**
     * Build a project from a projectDTO and store extra values as p cbor
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
                        JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(projectDTO,
                                JacksonMapper.typeRef));

        // Retrieve object spec
        ProjectBaseSpec<?> projectSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER
                .convertValue(
                        projectDTO.getSpec(),
                        ProjectBaseSpec.class
                );
        Map<String, Object> spec = projectSpec.toMap();

        return EntityFactory.combine(
                ConversionUtils.convert(projectDTO, "project"), projectDTO,
                builder -> builder
                        // check id
                        .withIfElse((projectDTO.getId() != null || projectDTO.getName() != null) &&
                                        projectDTO.getMetadata().getVersion() != null,
                                (p) -> {
                                    if (Optional.ofNullable(projectDTO.getId())
                                            .orElse(projectDTO.getName())
                                            .equals(projectDTO.getMetadata().getVersion())) {
                                        p.setId(projectDTO.getMetadata().getVersion());
                                    } else {
                                        throw new CoreException(
                                                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                                                "Trying to store item with which has different signature <id != version>",
                                                HttpStatus.INTERNAL_SERVER_ERROR
                                        );
                                    }
                                },
                                (p) -> {
                                    if (projectDTO.getId() == null &&
                                            projectDTO.getMetadata().getVersion() != null) {
                                        p.setId(projectDTO.getMetadata().getVersion());
                                    } else {
                                        p.setId(projectDTO.getId());
                                    }
                                })
                        .withIfElse(projectFieldAccessor.getState().equals(State.NONE.name()),
                                (p, condition) -> {
                                    if (condition) {
                                        p.setState(State.CREATED);
                                    } else {
                                        p.setState(State.valueOf(projectFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(p -> p.setMetadata(ConversionUtils.convert(
                                projectDTO.getMetadata(), "metadata")))
                        .with(p -> p.setExtra(ConversionUtils.convert(
                                projectDTO.getExtra(), "cbor")))
                        .with(p -> p.setStatus(ConversionUtils.convert(
                                projectDTO.getStatus(), "cbor")))
                        .with(p -> {
                            spec.remove("projects");
                            spec.remove("workflows");
                            spec.remove("artifacts");
                            spec.remove("dataitems");
                            p.setSpec(ConversionUtils.convert(spec, "cbor"));
                        })

                        // Metadata Extraction
                        .withIf(projectDTO.getMetadata().getSource() != null, (p) ->
                                p.setSource(projectDTO.getMetadata().getSource()))
                        .withIf(projectDTO.getMetadata().getDescription() != null, (p) ->
                                p.setDescription(projectDTO.getMetadata().getDescription()))
                        .withIf(projectDTO.getMetadata().getCreated() != null, (p) ->
                                p.setCreated(projectDTO.getMetadata().getCreated()))
                        .withIf(projectDTO.getMetadata().getUpdated() != null, (p) ->
                                p.setUpdated(projectDTO.getMetadata().getUpdated())
                        )


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
                        JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(projectDTO,
                                JacksonMapper.typeRef));

        // Retrieve object spec
        ProjectBaseSpec<?> projectSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER
                .convertValue(
                        projectDTO.getSpec(),
                        ProjectProjectSpec.class
                );
        Map<String, Object> spec = projectSpec.toMap();

        return EntityFactory.combine(
                project, projectDTO, builder -> builder

                        .withIfElse(projectFieldAccessor.getState().equals(State.NONE.name()),
                                (p, condition) -> {
                                    if (condition) {
                                        p.setState(State.CREATED);
                                    } else {
                                        p.setState(State.valueOf(projectFieldAccessor.getState()));
                                    }
                                }
                        )
                        .with(p -> p.setMetadata(ConversionUtils.convert(
                                projectDTO.getMetadata(), "metadata")))
                        .with(p -> p.setExtra(ConversionUtils.convert(
                                projectDTO.getExtra(), "cbor")))
                        .with(p -> p.setStatus(ConversionUtils.convert(
                                projectDTO.getStatus(), "cbor")))
                        .with(p -> {
                            spec.remove("projects");
                            spec.remove("workflows");
                            spec.remove("artifacts");
                            spec.remove("dataitems");
                            p.setSpec(ConversionUtils.convert(spec, "cbor"));
                        })
                        .withIf(projectDTO.getMetadata().getSource() != null, (p) ->
                                p.setSource(projectDTO.getMetadata().getSource()))
                        .withIf(projectDTO.getMetadata().getDescription() != null, (p) ->
                                p.setDescription(projectDTO.getMetadata().getDescription()))


        );
    }
}
