package it.smartcommunitylabdhub.core.models.entities.artifact.specs;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "artifact", entity = EntityName.ARTIFACT, factory = ArtifactArtifactSpec.class)
public class ArtifactArtifactSpec extends ArtifactBaseSpec<ArtifactArtifactSpec> {
    @Override
    protected void configureSpec(ArtifactArtifactSpec artifactArtifactSpec) {
        super.configureSpec(artifactArtifactSpec);

    }
}
