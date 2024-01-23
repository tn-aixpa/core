package it.smartcommunitylabdhub.core.models.entities.run.specs;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@SpecType(kind = "run", entity = EntityName.RUN, factory = RunRunSpec.class)
public class RunRunSpec extends RunBaseSpec {
    @Override
    public void configure(Map<String, Object> data) {

        RunRunSpec runRunSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, RunRunSpec.class);

        super.configure(data);
        this.setExtraSpecs(runRunSpec.getExtraSpecs());
    }
}

