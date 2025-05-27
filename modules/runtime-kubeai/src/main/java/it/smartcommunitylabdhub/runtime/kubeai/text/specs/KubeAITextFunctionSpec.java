package it.smartcommunitylabdhub.runtime.kubeai.text.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.runtime.kubeai.base.KubeAIServeFunctionSpec;
import it.smartcommunitylabdhub.runtime.kubeai.models.KubeAIEngine;
import it.smartcommunitylabdhub.runtime.kubeai.models.KubeAIFeature;
import it.smartcommunitylabdhub.runtime.kubeai.text.KubeAITextRuntime;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpecType(runtime = KubeAITextRuntime.RUNTIME, kind = KubeAITextRuntime.RUNTIME, entity = EntityName.FUNCTION)
public class KubeAITextFunctionSpec extends KubeAIServeFunctionSpec {

    @JsonProperty("features")
    @Schema(
        title = "fields.kubeai.features.title",
        description = "fields.kubeai.features.description",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Builder.Default
    private List<KubeAIFeature> features = List.of(KubeAIFeature.TextGeneration);

    @JsonProperty("engine")
    @Schema(
        title = "fields.kubeai.engine.title",
        description = "fields.kubeai.engine.description",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private KubeAIEngine engine;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        KubeAITextFunctionSpec spec = mapper.convertValue(data, KubeAITextFunctionSpec.class);
        this.engine = spec.getEngine();
        this.features = spec.getFeatures();
    }

    public static KubeAITextFunctionSpec with(Map<String, Serializable> data) {
        KubeAITextFunctionSpec spec = new KubeAITextFunctionSpec();
        spec.configure(data);
        return spec;
    }
}
