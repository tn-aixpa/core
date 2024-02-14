package it.smartcommunitylabdhub.runtime.dbt.specs.task;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.dbt.DbtRuntime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(runtime = DbtRuntime.RUNTIME, kind = "dbt+transform", entity = EntityName.TASK)
public class TaskTransformSpec extends K8sTaskBaseSpec {

    @Override
    public void configure(Map<String, Object> data) {
        TaskTransformSpec taskTransformSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
            data,
            TaskTransformSpec.class
        );

        super.configure(data);
        this.setExtraSpecs(taskTransformSpec.getExtraSpecs());
    }
}
