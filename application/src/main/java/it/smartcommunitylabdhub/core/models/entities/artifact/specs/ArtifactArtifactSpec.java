package it.smartcommunitylabdhub.core.models.entities.artifact.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.entities.artifact.specs.ArtifactBaseSpec;
import it.smartcommunitylabdhub.commons.utils.jackson.JacksonMapper;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(
  kind = "artifact",
  entity = EntityName.ARTIFACT,
  factory = ArtifactArtifactSpec.class
)
public class ArtifactArtifactSpec extends ArtifactBaseSpec {

  @Override
  public void configure(Map<String, Object> data) {
    ArtifactArtifactSpec artifactArtifactSpec =
      JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
        data,
        ArtifactArtifactSpec.class
      );

    super.configure(data);
    this.setExtraSpecs(artifactArtifactSpec.getExtraSpecs());
  }
}
