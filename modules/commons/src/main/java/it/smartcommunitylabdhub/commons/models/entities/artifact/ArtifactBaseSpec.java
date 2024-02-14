package it.smartcommunitylabdhub.commons.models.entities.artifact;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtifactBaseSpec extends BaseSpec {

    private String key;

    @JsonProperty("src_path")
    private String srcPath;

    @JsonProperty("target_path")
    private String targetPath;

    @Override
    public void configure(Map<String, Object> data) {
        ArtifactBaseSpec concreteSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(data, ArtifactBaseSpec.class);

        this.setKey(concreteSpec.getKey());
        this.setSrcPath(concreteSpec.getSrcPath());
        this.setTargetPath(concreteSpec.getTargetPath());

        super.configure(data);
        this.setExtraSpecs(concreteSpec.getExtraSpecs());
    }
}
