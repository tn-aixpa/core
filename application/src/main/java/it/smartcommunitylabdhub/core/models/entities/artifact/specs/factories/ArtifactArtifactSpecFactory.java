package it.smartcommunitylabdhub.core.models.entities.artifact.specs.factories;

import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.core.models.entities.artifact.specs.ArtifactArtifactSpec;
import org.springframework.stereotype.Component;

@Component
public class ArtifactArtifactSpecFactory implements SpecFactory<ArtifactArtifactSpec> {

    @Override
    public ArtifactArtifactSpec create() {
        return new ArtifactArtifactSpec();
    }
}
