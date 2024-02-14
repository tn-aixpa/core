package it.smartcommunitylabdhub.commons.models.schemas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import it.smartcommunitylabdhub.commons.infrastructure.enums.EntityName;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class SchemaImpl implements Schema, Serializable {

    private final String key;

    private final String kind;

    @JsonIgnore
    private final EntityName entity;

    @JsonIgnore
    private final transient JsonNode schema;

    @Override
    public String key() {
        return key;
    }

    @Override
    public String kind() {
        return kind;
    }

    @Override
    public String entity() {
        return entity.name();
    }

    @Override
    public JsonNode schema() {
        return schema;
    }
}
