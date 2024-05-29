package it.smartcommunitylabdhub.commons.models.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
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
@SpecType(kind = "metadata.embedded", entity = EntityName.METADATA)
public final class EmbeddableMetadata extends BaseSpec implements Metadata {

    private Boolean embedded;

    @Override
    public void configure(Map<String, Serializable> data) {
        EmbeddableMetadata meta = mapper.convertValue(data, EmbeddableMetadata.class);

        this.embedded = meta.getEmbedded();
    }

    public static EmbeddableMetadata from(Map<String, Serializable> map) {
        EmbeddableMetadata meta = new EmbeddableMetadata();
        meta.configure(map);

        return meta;
    }
}
