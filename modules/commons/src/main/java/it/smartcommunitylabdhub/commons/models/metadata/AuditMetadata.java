package it.smartcommunitylabdhub.commons.models.metadata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import java.io.Serializable;
import java.time.OffsetDateTime;
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
@SpecType(kind = "metadata.audit", entity = EntityName.METADATA)
public final class AuditMetadata extends BaseSpec implements Metadata {

    @JsonProperty("created_by")
    protected String createdBy;

    @JsonProperty("updated_by")
    protected String updatedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected OffsetDateTime created;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected OffsetDateTime updated;

    @Override
    public void configure(Map<String, Serializable> data) {
        AuditMetadata meta = mapper.convertValue(data, AuditMetadata.class);

        this.createdBy = meta.getCreatedBy();
        this.updatedBy = meta.getUpdatedBy();

        this.created = meta.getCreated();
        this.updated = meta.getUpdated();
    }

    public static AuditMetadata from(Map<String, Serializable> map) {
        AuditMetadata meta = new AuditMetadata();
        meta.configure(map);

        return meta;
    }
}
