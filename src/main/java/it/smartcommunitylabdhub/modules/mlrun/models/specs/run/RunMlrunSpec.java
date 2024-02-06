package it.smartcommunitylabdhub.modules.mlrun.models.specs.run;


import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.modules.mlrun.models.specs.function.FunctionMlrunSpec;
import it.smartcommunitylabdhub.modules.mlrun.models.specs.task.TaskMlrunSpec;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@SpecType(kind = "run+mlrun", entity = EntityName.RUN, factory = RunMlrunSpec.class)
public class RunMlrunSpec extends RunBaseSpec {

    private TaskMlrunSpec taskMlrunSpec;

    private FunctionMlrunSpec functionMlrunSpec;

    @Override
    public void configure(Map<String, Object> data) {

        RunMlrunSpec runMlrunSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, RunMlrunSpec.class);

        super.configure(data);
        this.setExtraSpecs(runMlrunSpec.getExtraSpecs());
    }
}