package it.smartcommunitylabdhub.runtime.mlrun.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.framework.k8s.base.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.runtime.mlrun.MlrunRuntime;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = MlrunRuntime.RUNTIME, kind = MlrunBuildTaskSpec.KIND, entity = EntityName.TASK)
public class MlrunBuildTaskSpec extends K8sTaskBaseSpec {

    public static final String KIND = "mlrun+build";

    private List<String> commands;

    @JsonProperty("force_build")
    private Boolean forceBuild;

    @JsonProperty("target_image")
    private String targetImage;

    public MlrunBuildTaskSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        MlrunBuildTaskSpec spec = mapper.convertValue(data, MlrunBuildTaskSpec.class);

        this.commands = spec.getCommands();
        this.forceBuild = spec.getForceBuild();
        this.targetImage = spec.getTargetImage();
    }
}
