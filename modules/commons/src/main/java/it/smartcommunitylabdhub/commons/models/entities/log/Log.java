package it.smartcommunitylabdhub.commons.models.entities.log;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.smartcommunitylabdhub.commons.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonPropertyOrder(alphabetic = true)
public class Log implements BaseDTO {

    @ValidateField(allowNull = true, fieldType = "uuid", message = "Invalid UUID4 string")
    private String id;

    private String project;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Serializable> body = new HashMap<>();

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

    @Override
    public @NotNull String getName() {
        return id;
    }

    @Override
    public @NotNull String getKind() {
        return "log";
    }

    @Override
    public Map<String, Serializable> getSpec() {
        return body;
    }

    @Override
    public void setSpec(Map<String, Serializable> spec) {
        // nothing to do
    }
}
