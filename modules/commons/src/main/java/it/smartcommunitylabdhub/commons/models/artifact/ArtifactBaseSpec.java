package it.smartcommunitylabdhub.commons.models.artifact;

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
public class ArtifactBaseSpec extends BaseSpec {

    @JsonProperty("src_path")
    private String srcPath;

    @NotBlank
    @JsonProperty("path")
    private String path;

    @Override
    public void configure(Map<String, Serializable> data) {
        ArtifactBaseSpec spec = mapper.convertValue(data, ArtifactBaseSpec.class);

        this.srcPath = spec.getSrcPath();
        this.path = spec.getPath();
    }
}
