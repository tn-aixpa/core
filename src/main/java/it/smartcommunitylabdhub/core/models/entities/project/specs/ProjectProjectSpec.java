package it.smartcommunitylabdhub.core.models.entities.project.specs;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;

import java.util.Map;

@SpecType(kind = "project", entity = EntityName.PROJECT, factory = ProjectProjectSpec.class)
public class ProjectProjectSpec extends ProjectBaseSpec {
    @Override
    public void configure(Map<String, Object> data) {

        ProjectProjectSpec projectProjectSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, ProjectProjectSpec.class);
        super.configure(data);

        this.setExtraSpecs(projectProjectSpec.getExtraSpecs());
    }
}
