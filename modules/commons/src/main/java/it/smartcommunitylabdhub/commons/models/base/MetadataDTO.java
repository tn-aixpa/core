package it.smartcommunitylabdhub.commons.models.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.Map;

@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface MetadataDTO extends Serializable {
    Map<String, Serializable> getMetadata();

    void setMetadata(Map<String, Serializable> metadata);
}
