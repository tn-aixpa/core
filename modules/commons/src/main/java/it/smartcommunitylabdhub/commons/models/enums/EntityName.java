package it.smartcommunitylabdhub.commons.models.enums;

import lombok.Getter;
import lombok.ToString;

//
@Getter
@ToString
public enum EntityName {
    NONE("none"),
    PROJECT("project"),
    WORKFLOW("workflow"),
    FUNCTION("function"),
    SECRET("secret"),
    ARTIFACT("artifact"),
    DATAITEM("dataitem"),
    TASK("task"),
    RUN("run"),
    LOG("log");

    private final String value;

    EntityName(String value) {
        this.value = value;
    }
}
