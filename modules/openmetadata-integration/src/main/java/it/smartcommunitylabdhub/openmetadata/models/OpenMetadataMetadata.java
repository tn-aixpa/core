package it.smartcommunitylabdhub.openmetadata.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.metadata.Metadata;
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
@SpecType(kind = "metadata.openmetadata", entity = EntityName.METADATA)
public final class OpenMetadataMetadata extends BaseSpec implements Metadata {

    @JsonProperty("openmetadata")
    private OpenMetadataDetails openMetadata;

    @Override
    public void configure(Map<String, Serializable> data) {
        OpenMetadataMetadata meta = mapper.convertValue(data, OpenMetadataMetadata.class);

        this.openMetadata = meta.getOpenMetadata();
    }
}
