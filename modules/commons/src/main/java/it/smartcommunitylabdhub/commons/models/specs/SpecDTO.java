package it.smartcommunitylabdhub.commons.models.specs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.Map;

@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface SpecDTO extends Serializable {
    Map<String, Serializable> getSpec();

    void setSpec(Map<String, Serializable> spec);
}
