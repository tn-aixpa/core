package it.smartcommunitylabdhub.commons.models.metadata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder(builderMethodName = "baseBuilder")
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@SpecType(kind = "metadata.base", entity = EntityName.METADATA)
public final class BaseMetadata extends BaseSpec implements Metadata {

    protected String project;

    protected String name;
    protected String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected OffsetDateTime created;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    protected OffsetDateTime updated;

    protected Set<String> labels;

    @Override
    public void configure(Map<String, Serializable> data) {
        BaseMetadata meta = mapper.convertValue(data, BaseMetadata.class);

        this.project = meta.getProject();

        this.name = meta.getName();
        this.description = meta.getDescription();

        this.created = meta.getCreated();
        this.updated = meta.getUpdated();

        this.labels = meta.getLabels();
    }

    public static BaseMetadata from(Map<String, Serializable> map) {
        BaseMetadata meta = new BaseMetadata();
        meta.configure(map);

        return meta;
    }
}
