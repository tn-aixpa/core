package it.smartcommunitylabdhub.commons.models.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.specs.Spec;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public abstract class BaseSpec implements Spec {

    @JsonIgnore
    protected static final ObjectMapper mapper = JacksonMapper.CUSTOM_OBJECT_MAPPER;

    @JsonIgnore
    protected static final TypeReference<HashMap<String, Serializable>> typeRef = new TypeReference<
        HashMap<String, Serializable>
    >() {};

    @Override
    public Map<String, Serializable> toMap() {
        return mapper.convertValue(this, typeRef);
    }
}
