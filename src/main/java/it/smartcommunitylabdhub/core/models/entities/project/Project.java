package it.smartcommunitylabdhub.core.models.entities.project;

import com.fasterxml.jackson.annotation.*;
import it.smartcommunitylabdhub.core.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.core.models.base.abstracts.AbstractExtractorProperties;
import it.smartcommunitylabdhub.core.models.base.interfaces.BaseEntity;
import it.smartcommunitylabdhub.core.models.entities.project.metadata.ProjectMetadata;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonPropertyOrder(alphabetic = true)
public class Project extends AbstractExtractorProperties implements BaseEntity {

    @JsonIgnore
    private String id;

    @NotNull
    @ValidateField
    private String name;

    @NotNull
    @ValidateField
    private String kind;

    private String description;
    private String source;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> spec = new HashMap<>();

    @Builder.Default
    @JsonIgnore
    private Map<String, Object> extra = new HashMap<>();

    @Builder.Default
    private Map<String, Object> status = new HashMap<>();

    private Date created;

    private Date updated;

    private ProjectMetadata metadata;


    @JsonAnyGetter
    public Map<String, Object> getExtra() {
        return this.extra;
    }

    @JsonAnySetter
    public void setExtra(String key, Object value) {
        if (value != null) {
            extra.put(key, value);
        }
    }

    @JsonProperty("id")
    private String getExposedId() {
        return id;
    }

}
