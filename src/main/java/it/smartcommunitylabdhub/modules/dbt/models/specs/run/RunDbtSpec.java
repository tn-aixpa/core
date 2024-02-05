package it.smartcommunitylabdhub.modules.dbt.models.specs.run;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.task.specs.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.modules.dbt.models.specs.function.FunctionDbtSpec;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@SpecType(kind = "run+dbt", entity = EntityName.RUN, factory = RunDbtSpec.class)
public class RunDbtSpec extends RunBaseSpec {

    private K8sTaskBaseSpec k8sTaskBaseSpec;

    private FunctionDbtSpec functionDbtSpec;

    @Override
    public void configure(Map<String, Object> data) {

        RunDbtSpec runDbtSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, RunDbtSpec.class);

        super.configure(data);
        this.setExtraSpecs(runDbtSpec.getExtraSpecs());
    }
}