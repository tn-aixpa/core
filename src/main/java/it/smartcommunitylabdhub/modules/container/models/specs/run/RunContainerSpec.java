package it.smartcommunitylabdhub.modules.container.models.specs.run;


import com.fasterxml.jackson.core.type.TypeReference;
import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunBaseSpec;
import it.smartcommunitylabdhub.core.models.entities.task.specs.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import it.smartcommunitylabdhub.modules.container.models.specs.function.FunctionContainerSpec;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpecType(kind = "run+container", entity = EntityName.RUN, factory = RunContainerSpec.class)
public class RunContainerSpec<T extends K8sTaskBaseSpec> extends RunBaseSpec {

    private T k8sTaskBaseSpec;

    private FunctionContainerSpec functionContainerSpec;

    @Override
    public void configure(Map<String, Object> data) {

        TypeReference<RunContainerSpec<T>> typeReference = new TypeReference<>() {
        };
        RunContainerSpec<T> runContainerSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, typeReference);


        super.configure(data);
        this.setExtraSpecs(runContainerSpec.getExtraSpecs());
    }
}