package it.smartcommunitylabdhub.commons.models.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface BaseDTO extends Serializable {
    String getId();

    void setId(String id);

    String getName();

    @NotNull
    String getKind();

    String getProject();

    String getUser();

    String getKey();

    Map<String, Serializable> getMetadata();

    void setMetadata(Map<String, Serializable> metadata);

    Map<String, Serializable> getSpec();

    void setSpec(Map<String, Serializable> spec);

    Map<String, Serializable> getStatus();

    void setStatus(Map<String, Serializable> status);
}
