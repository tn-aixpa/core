package it.smartcommunitylabdhub.core.models.specs.artifact;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.artifact.ArtifactBaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;

import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "artifact", entity = EntityName.ARTIFACT)
public class ArtifactArtifactSpec extends ArtifactBaseSpec {

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);
    }
}
