package it.smartcommunitylabdhub.commons.models.specs;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public abstract class ConcreteSpecMixin {

    @JsonAnySetter
    public abstract void handleUnknownProperties(String key, Object value);
}
