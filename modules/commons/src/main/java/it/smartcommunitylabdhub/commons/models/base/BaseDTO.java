package it.smartcommunitylabdhub.commons.models.base;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@JsonPropertyOrder(alphabetic = true)
public interface BaseDTO extends Serializable {
    String getId();

    void setId(String id);

    String getName();

    @NotNull
    String getKind();

    String getProject();

    Map<String, Serializable> getMetadata();

    void setMetadata(Map<String, Serializable> metadata);

    Map<String, Serializable> getSpec();

    void setSpec(Map<String, Serializable> spec);

    Map<String, Serializable> getStatus();

    void setStatus(Map<String, Serializable> status);

    Map<String, Serializable> getExtra();

    void setExtra(Map<String, Serializable> extra);
}
