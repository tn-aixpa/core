package it.smartcommunitylabdhub.runtime.container.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sFunctionTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.container.ContainerRuntime;
import jakarta.validation.constraints.Min;
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
@SpecType(runtime = ContainerRuntime.RUNTIME, kind = ContainerJobTaskSpec.KIND, entity = EntityName.TASK)
public class ContainerJobTaskSpec extends K8sFunctionTaskBaseSpec {

    public static final String KIND = "container+job";

    @Pattern(regexp = Keys.CRONTAB_PATTERN)
    private String schedule;

    @JsonProperty("fs_group")
    @Min(1)
    private Integer fsGroup;

    @JsonProperty("run_as_user")
    @Min(1)
    private Integer runAsUser;

    @JsonProperty("run_as_group")
    @Min(1)
    private Integer runAsGroup;

    public ContainerJobTaskSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        ContainerJobTaskSpec spec = mapper.convertValue(data, ContainerJobTaskSpec.class);
        this.schedule = spec.getSchedule();
        this.fsGroup = spec.getFsGroup();
        this.runAsGroup = spec.getRunAsUser();
        this.runAsGroup = spec.getRunAsGroup();
    }
}
