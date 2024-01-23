package it.smartcommunitylabdhub.core.models.entities.artifact.specs;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@SpecType(kind = "artifact", entity = EntityName.ARTIFACT, factory = ArtifactArtifactSpec.class)
public class ArtifactArtifactSpec extends ArtifactBaseSpec {
    @Override
    public void configure(Map<String, Object> data) {

        ArtifactArtifactSpec artifactArtifactSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, ArtifactArtifactSpec.class);
        
        super.configure(data);
        this.setExtraSpecs(artifactArtifactSpec.getExtraSpecs());

    }
}
