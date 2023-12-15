package it.smartcommunitylabdhub.core.models.base.specs;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import it.smartcommunitylabdhub.core.models.base.interfaces.Spec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseSpec<S extends Spec> implements Spec {

    private Map<String, Object> extraSpecs = new HashMap<>();

    @Override
    public void configure(Map<String, Object> data) {
        // Retrieve concreteSpec
        S concreteSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, JacksonMapper.extractJavaType(this.getClass()));
        configureSpec(concreteSpec);
    }

    protected abstract void configureSpec(S concreteSpec);

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();

        // Serialize all fields (including extraSpecs) to a JSON map
        try {
            String json = JacksonMapper.CUSTOM_OBJECT_MAPPER.writeValueAsString(this);

            // Convert the JSON string to a map
            Map<String, Object> serializedMap =
                    JacksonMapper.CUSTOM_OBJECT_MAPPER.readValue(json, JacksonMapper.typeRef);

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
