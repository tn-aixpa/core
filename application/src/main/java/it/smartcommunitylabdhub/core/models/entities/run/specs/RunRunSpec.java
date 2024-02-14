package it.smartcommunitylabdhub.core.models.entities.run.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.run.RunBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "run", entity = EntityName.RUN)
public class RunRunSpec extends RunBaseSpec {

    @Override
    public void configure(Map<String, Object> data) {
        RunRunSpec runRunSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, RunRunSpec.class);

        super.configure(data);
        this.setExtraSpecs(runRunSpec.getExtraSpecs());
    }
}
