package it.smartcommunitylabdhub.models.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ModelBaseSpec extends BaseSpec {

    @NotBlank
    @JsonProperty("path")
    private String path;

    @JsonProperty("framework")
    private String framework;

    @JsonProperty("algorithm")
    private String algorithm;

    @Override
    public void configure(Map<String, Serializable> data) {
        ModelBaseSpec spec = mapper.convertValue(data, ModelBaseSpec.class);

        this.path = spec.getPath();
        this.framework = spec.getFramework();
        this.algorithm = spec.getAlgorithm();
    }
}
