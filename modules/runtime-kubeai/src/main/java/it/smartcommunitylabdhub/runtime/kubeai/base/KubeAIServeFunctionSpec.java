package it.smartcommunitylabdhub.runtime.kubeai.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.models.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.runtime.kubeai.models.KubeAIAdapter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KubeAIServeFunctionSpec extends FunctionBaseSpec {

    @NotNull
    @Pattern(
        regexp = "^(store://([^/]+)/model/huggingface/.*)" +
        "|" +
        "^pvc?://.*$" +
        "|" +
        "^s3?://.*$" +
        "|" +
        "^ollama?://.*$" +
        "|" +
        "^hf?://.*$"
    )
    @Schema(title = "fields.kubeai.url.title", description = "fields.kubeai.url.description")
    private String url;

    @JsonProperty("model_name")
    @Schema(
        title = "fields.modelserve.modelname.title",
        description = "fields.modelserve.modelname.description",
        defaultValue = "model"
    )
    private String modelName;

    @JsonProperty("image")
    @Schema(title = "fields.kubeai.image.title", description = "fields.kubeai.image.description")
    private String image;

    @JsonProperty("adapters")
    @Schema(title = "fields.kubeai.adapters.title", description = "fields.kubeai.adapters.description")
    private List<KubeAIAdapter> adapters;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        KubeAIServeFunctionSpec spec = mapper.convertValue(data, KubeAIServeFunctionSpec.class);
        this.modelName = spec.getModelName();
        this.image = spec.getImage();
        this.url = spec.getUrl();
        this.adapters = spec.getAdapters();
    }

    public static KubeAIServeFunctionSpec with(Map<String, Serializable> data) {
        KubeAIServeFunctionSpec spec = new KubeAIServeFunctionSpec();
        spec.configure(data);
        return spec;
    }
}
