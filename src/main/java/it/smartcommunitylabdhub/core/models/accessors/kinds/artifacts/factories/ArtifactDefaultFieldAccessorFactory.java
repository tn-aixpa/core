package it.smartcommunitylabdhub.core.models.accessors.kinds.artifacts.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.core.models.accessors.kinds.artifacts.ArtifactDefaultFieldAccessor;
import org.springframework.stereotype.Component;

@Component
public class ArtifactDefaultFieldAccessorFactory implements AccessorFactory<ArtifactDefaultFieldAccessor> {
    @Override
    public ArtifactDefaultFieldAccessor create() {
        return new ArtifactDefaultFieldAccessor();
    }
}
