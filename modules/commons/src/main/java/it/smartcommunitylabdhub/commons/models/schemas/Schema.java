package it.smartcommunitylabdhub.commons.models.schemas;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;

public interface Schema {
    @JsonGetter
    String kind();

    @JsonGetter
    String runtime();

    @JsonGetter
    String entity();

    @JsonIgnore
    JsonNode schema();

    @JsonRawValue
    default String getSchema() {
        return schema() == null ? null : schema().toString();
    }

    @JsonGetter
    default String getId() {
        return entity() + ":" + kind();
    }
}
