package it.smartcommunitylabdhub.core.models.entities.artifact.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.artifact.ArtifactBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "artifact", entity = EntityName.ARTIFACT)
public class ArtifactArtifactSpec extends ArtifactBaseSpec {

    @Override
    public void configure(Map<String, Object> data) {
        ArtifactArtifactSpec artifactArtifactSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
            data,
            ArtifactArtifactSpec.class
        );

        super.configure(data);
        this.setExtraSpecs(artifactArtifactSpec.getExtraSpecs());
    }
}
