package it.smartcommunitylabdhub.commons.models.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@SpecType(kind = "metadata.versioning", entity = EntityName.METADATA)
public final class VersioningMetadata extends BaseSpec implements Metadata {

    protected String project;
    protected String name;
    private String version;

    @Override
    public void configure(Map<String, Serializable> data) {
        VersioningMetadata meta = mapper.convertValue(data, VersioningMetadata.class);

        this.project = meta.getProject();
        this.name = meta.getName();
        this.version = meta.getVersion();
    }

    public static VersioningMetadata from(Map<String, Serializable> map) {
        VersioningMetadata meta = new VersioningMetadata();
        meta.configure(map);

        return meta;
    }
}
