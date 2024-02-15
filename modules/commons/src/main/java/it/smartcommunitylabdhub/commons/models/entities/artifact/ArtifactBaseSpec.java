package it.smartcommunitylabdhub.commons.models.entities.artifact;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArtifactBaseSpec extends BaseSpec {

    private String key;

    @JsonProperty("src_path")
    private String srcPath;

    @JsonProperty("target_path")
    private String targetPath;

    @Override
    public void configure(Map<String, Serializable> data) {
        ArtifactBaseSpec concreteSpec = mapper.convertValue(data, ArtifactBaseSpec.class);

        this.setKey(concreteSpec.getKey());
        this.setSrcPath(concreteSpec.getSrcPath());
        this.setTargetPath(concreteSpec.getTargetPath());
    }
}
