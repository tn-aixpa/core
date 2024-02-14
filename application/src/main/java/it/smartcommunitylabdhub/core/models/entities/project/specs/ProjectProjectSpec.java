package it.smartcommunitylabdhub.core.models.entities.project.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.project.ProjectBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;

import java.util.Map;

@SpecType(kind = "project", entity = EntityName.PROJECT)
public class ProjectProjectSpec extends ProjectBaseSpec {

    @Override
    public void configure(Map<String, Object> data) {
        ProjectProjectSpec projectProjectSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
            data,
            ProjectProjectSpec.class
        );
        super.configure(data);

        this.setExtraSpecs(projectProjectSpec.getExtraSpecs());
    }
}
