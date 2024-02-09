package it.smartcommunitylabdhub.core.models.entities.project.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.entities.project.specs.ProjectBaseSpec;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import java.util.Map;

@SpecType(
  kind = "project",
  entity = EntityName.PROJECT,
  factory = ProjectProjectSpec.class
)
public class ProjectProjectSpec extends ProjectBaseSpec {

  @Override
  public void configure(Map<String, Object> data) {
    ProjectProjectSpec projectProjectSpec =
      JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
        data,
        ProjectProjectSpec.class
      );
    super.configure(data);

    this.setExtraSpecs(projectProjectSpec.getExtraSpecs());
  }
}
