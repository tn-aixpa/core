package it.smartcommunitylabdhub.core.models.builders.project;

import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecRegistry;
import it.smartcommunitylabdhub.commons.models.accessors.kinds.interfaces.ProjectFieldAccessor;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.entities.project.specs.ProjectBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.State;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.core.models.builders.EntityFactory;
import it.smartcommunitylabdhub.core.models.converters.ConversionUtils;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectEntityBuilder {

  @Autowired
  SpecRegistry specRegistry;

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
    ProjectFieldAccessor projectFieldAccessor = ProjectFieldAccessor.with(
      JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
        projectDTO,
        JacksonMapper.typeRef
      )
    );

    // Retrieve object spec
    ProjectBaseSpec projectSpec =
      JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
        projectDTO.getSpec(),
        ProjectBaseSpec.class
      );
    Map<String, Object> spec = projectSpec.toMap();

    return EntityFactory.combine(
      ProjectEntity.builder().build(),
      projectDTO,
      builder ->
        builder
          // check id
          .withIf(projectDTO.getId() != null, p -> p.setId(projectDTO.getId()))
          .with(p -> p.setName(projectDTO.getName()))
          .with(p -> p.setKind(projectDTO.getKind()))
          .withIfElse(
            projectFieldAccessor.getState().equals(State.NONE.name()),
            (p, condition) -> {
              if (condition) {
                p.setState(State.CREATED);
              } else {
                p.setState(State.valueOf(projectFieldAccessor.getState()));
              }
            }
          )
          .with(p ->
            p.setMetadata(
              ConversionUtils.convert(projectDTO.getMetadata(), "metadata")
            )
          )
          .with(p ->
            p.setExtra(ConversionUtils.convert(projectDTO.getExtra(), "cbor"))
          )
          .with(p ->
            p.setStatus(ConversionUtils.convert(projectDTO.getStatus(), "cbor"))
          )
          .with(p -> {
            spec.remove("projects");
            spec.remove("workflows");
            spec.remove("artifacts");
            spec.remove("dataitems");
            p.setSpec(ConversionUtils.convert(spec, "cbor"));
          })
          // Metadata Extraction
          .withIf(
            projectDTO.getMetadata().getSource() != null,
            p -> p.setSource(projectDTO.getMetadata().getSource())
          )
          .withIf(
            projectDTO.getMetadata().getDescription() != null,
            p -> p.setDescription(projectDTO.getMetadata().getDescription())
          )
          .withIf(
            projectDTO.getMetadata().getCreated() != null,
            p -> p.setCreated(projectDTO.getMetadata().getCreated())
          )
          .withIf(
            projectDTO.getMetadata().getUpdated() != null,
            p -> p.setUpdated(projectDTO.getMetadata().getUpdated())
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
    ProjectEntity newProject = build(projectDTO);
    return doUpdate(project, newProject);
  }

  private ProjectEntity doUpdate(
    ProjectEntity project,
    ProjectEntity newProject
  ) {
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
          .with(p -> p.setMetadata(newProject.getMetadata()))
          .with(p -> p.setExtra(newProject.getExtra()))
          .with(p -> p.setStatus(newProject.getStatus()))
          .with(p -> p.setSpec(newProject.getSpec()))
          .withIf(
            newProject.getSource() != null,
            p -> p.setSource(newProject.getSource())
          )
          .withIf(
            newProject.getDescription() != null,
            p -> p.setDescription(newProject.getDescription())
          )
    );
  }
}
