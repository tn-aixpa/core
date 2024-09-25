package it.smartcommunitylabdhub.runtime.kfp.specs;

import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.kfp.KFPRuntime;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SpecType(runtime = KFPRuntime.RUNTIME, kind = KFPPipelineTaskSpec.KIND, entity = EntityName.TASK)
public class KFPPipelineTaskSpec extends K8sTaskBaseSpec {

    public static final String KIND = "kfp+pipeline";

    @Pattern(regexp = Keys.CRONTAB_PATTERN)
    private String schedule;

    public KFPPipelineTaskSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        KFPPipelineTaskSpec spec = mapper.convertValue(data, KFPPipelineTaskSpec.class);
        this.schedule = spec.getSchedule();
    }
}
