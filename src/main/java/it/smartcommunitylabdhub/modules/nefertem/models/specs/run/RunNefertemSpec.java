package it.smartcommunitylabdhub.modules.nefertem.models.specs.run;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.task.specs.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.function.FunctionNefertemSpec;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpecType(kind = "run+nefertem", entity = EntityName.RUN, factory = RunNefertemSpec.class)
public class RunNefertemSpec<T extends K8sTaskBaseSpec> extends RunBaseSpec {

    @JsonProperty("task_spec")
    private T taskSpec;

    @JsonProperty("func_spec")
    private FunctionNefertemSpec funcSpec;

    @Override
    public void configure(Map<String, Object> data) {

        TypeReference<RunNefertemSpec<T>> typeReference = new TypeReference<>() {
        };
        RunNefertemSpec<T> runNefertemSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, typeReference);

        this.setTaskSpec(runNefertemSpec.getTaskSpec());
        this.setFuncSpec(runNefertemSpec.getFuncSpec());

        super.configure(data);
        this.setExtraSpecs(runNefertemSpec.getExtraSpecs());
    }
}