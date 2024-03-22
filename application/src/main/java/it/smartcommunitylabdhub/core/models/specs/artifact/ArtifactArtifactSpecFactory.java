package it.smartcommunitylabdhub.core.models.specs.artifact;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ArtifactArtifactSpecFactory implements SpecFactory<ArtifactArtifactSpec> {

    @Override
    public ArtifactArtifactSpec create() {
        return new ArtifactArtifactSpec();
    }

    @Override
    public ArtifactArtifactSpec create(Map<String, Serializable> data) {
        ArtifactArtifactSpec spec = new ArtifactArtifactSpec();
        spec.configure(data);

        return spec;
    }
}
