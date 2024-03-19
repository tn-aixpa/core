package it.smartcommunitylabdhub.commons.models.entities.artifact;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.base.BaseMetadata;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtifactMetadata extends BaseMetadata {

    private String version;

    private Boolean embedded;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        ArtifactMetadata meta = mapper.convertValue(data, ArtifactMetadata.class);

        this.version = meta.getVersion();
        this.embedded = meta.getEmbedded();
    }
}
