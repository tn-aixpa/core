package it.smartcommunitylabdhub.core.artifacts.relationships;

import it.smartcommunitylabdhub.commons.models.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.core.artifacts.persistence.ArtifactEntity;
import it.smartcommunitylabdhub.core.relationships.base.BaseEntityRelationshipsManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ArtifactEntityRelationshipsManager extends BaseEntityRelationshipsManager<Artifact, ArtifactEntity> {

    private static final EntityName TYPE = EntityName.ARTIFACT;

    @Override
    protected EntityName getType() {
        return TYPE;
    }
}
