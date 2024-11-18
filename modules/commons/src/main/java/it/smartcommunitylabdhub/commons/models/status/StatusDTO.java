package it.smartcommunitylabdhub.commons.models.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.Serializable;
import java.util.Map;

@JsonPropertyOrder(alphabetic = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface StatusDTO extends Serializable {
    Map<String, Serializable> getStatus();

    void setStatus(Map<String, Serializable> status);
}
