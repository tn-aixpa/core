package it.smartcommunitylabdhub.commons.models.entities.project;

import com.fasterxml.jackson.annotation.*;
import it.smartcommunitylabdhub.commons.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonPropertyOrder(alphabetic = true)
public class Project implements BaseDTO {

    private String id;

    @NotNull
    @ValidateField
    private String name;

    @ValidateField
    private String kind = "project";

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Serializable> spec = new HashMap<>();

    @Builder.Default
    @JsonIgnore
    private Map<String, Serializable> extra = new HashMap<>();

    @Builder.Default
    private Map<String, Serializable> status = new HashMap<>();

    @Builder.Default
    private Map<String, Serializable> metadata = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Serializable> getExtra() {
        return this.extra;
    }

    @JsonAnySetter
    public void setExtra(String key, Serializable value) {
        if (value != null) {
            extra.put(key, value);
        }
    }
}
