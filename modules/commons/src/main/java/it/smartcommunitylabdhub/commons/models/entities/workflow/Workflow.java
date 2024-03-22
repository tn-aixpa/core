package it.smartcommunitylabdhub.commons.models.entities.workflow;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.models.base.BaseDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@JsonPropertyOrder(alphabetic = true)
public class Workflow implements BaseDTO {

    @Nullable
    @Pattern(regexp = Keys.SLUG_PATTERN)
    private String id;

    @NotNull
    @Pattern(regexp = Keys.SLUG_PATTERN)
    private String name;

    @NotNull
    @Pattern(regexp = Keys.SLUG_PATTERN)
    private String kind;

    @NotNull
    @Pattern(regexp = Keys.SLUG_PATTERN)
    private String project;

    @Builder.Default
    private Map<String, Serializable> metadata = new HashMap<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Serializable> spec = new HashMap<>();

    @Builder.Default
    private Map<String, Serializable> status = new HashMap<>();

    @Builder.Default
    @JsonIgnore
    @ToString.Exclude
    private Map<String, Serializable> extra = new HashMap<>();

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
    public String getKey() {
        StringBuilder sb = new StringBuilder();
        sb.append(Keys.STORE_PREFIX).append(getProject());
        sb.append(Keys.PATH_DIVIDER).append("workflows");
        sb.append(Keys.PATH_DIVIDER).append(getKind());
        sb.append(Keys.PATH_DIVIDER).append(getName());
        if (getId() != null) {
            sb.append(Keys.ID_DIVIDER).append(getId());
        }

        return sb.toString();
    }
}
