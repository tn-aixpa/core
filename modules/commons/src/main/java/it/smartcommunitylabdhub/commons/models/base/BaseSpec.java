package it.smartcommunitylabdhub.commons.models.base;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.jackson.annotations.JsonSchemaIgnore;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseSpec implements Spec {

    @JsonSchemaIgnore
    private Map<String, Object> extraSpecs = new HashMap<>();

    @Override
    public void configure(Map<String, Object> data) {}

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        // Serialize all fields (including extraSpecs) to a JSON map
        try {
            String json = JacksonMapper.CUSTOM_OBJECT_MAPPER.writeValueAsString(this);

            // Convert the JSON string to a map
            Map<String, Object> serializedMap = JacksonMapper.CUSTOM_OBJECT_MAPPER.readValue(
                json,
                JacksonMapper.typeRef
            );

            // Include extra properties in the result map
            result.putAll(serializedMap);
            result.putAll(extraSpecs);

            return result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting to Map", e);
        }
    }

    @JsonAnyGetter
    public Map<String, Object> getExtraSpecs() {
        return extraSpecs;
    }

    @JsonAnySetter
    public void setExtraSpecs(String key, Object value) {
        this.extraSpecs.put(key, value);
    }
}
