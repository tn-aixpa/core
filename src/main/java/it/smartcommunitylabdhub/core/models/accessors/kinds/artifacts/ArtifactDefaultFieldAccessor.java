package it.smartcommunitylabdhub.core.models.accessors.kinds.artifacts;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.kinds.abstracts.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.ArtifactFieldAccessor;

@AccessorType(kind = "artifact", entity = EntityName.ARTIFACT)
public class ArtifactDefaultFieldAccessor
        extends AbstractFieldAccessor<ArtifactDefaultFieldAccessor>
        implements ArtifactFieldAccessor<ArtifactDefaultFieldAccessor> {
}
