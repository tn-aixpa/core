package it.smartcommunitylabdhub.commons.models.base;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@JsonPropertyOrder(alphabetic = true)
public interface BaseDTO extends Serializable {
    String getId();

    @NotNull
    String getName();

    @NotNull
    String getKind();

    String getProject();

    Map<String, Serializable> getMetadata();
    Map<String, Serializable> getSpec();
    Map<String, Serializable> getStatus();
    Map<String, Serializable> getExtra();
}
