package it.smartcommunitylabdhub.core.models.entities.project;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.core.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.core.models.base.interfaces.BaseEntity;
import it.smartcommunitylabdhub.core.models.entities.StatusFieldUtility;
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
public class Project implements BaseEntity {

    @JsonIgnore
    @ValidateField(allowNull = true, fieldType = "uuid", message = "Invalid UUID4 string")
    private String id;

    @NotNull
    @ValidateField
    private String name;

    @NotNull
    @ValidateField
    private String kind;

    private String description;
    private String source;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Builder.Default
    private Map<String, Object> spec = new HashMap<>();

    @Builder.Default
    @JsonIgnore
    private Map<String, Object> extra = new HashMap<>();

    private Date created;

    private Date updated;

    @JsonIgnore
    private String state;

    private ProjectMetadata metadata;


    @JsonAnyGetter
    public Map<String, Object> getExtra() {
        return StatusFieldUtility.addStatusField(extra, state);

    }

    @JsonAnySetter
    public void setExtra(String key, Object value) {
        if (value != null) {
            extra.put(key, value);
            StatusFieldUtility.updateStateField(this);
        }
    }

}
