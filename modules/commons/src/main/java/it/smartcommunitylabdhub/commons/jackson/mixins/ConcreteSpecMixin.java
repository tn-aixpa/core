package it.smartcommunitylabdhub.commons.jackson.mixins;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public abstract class ConcreteSpecMixin {

    @JsonAnySetter
    public abstract void handleUnknownProperties(String key, Object value);
}
