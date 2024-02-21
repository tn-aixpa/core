package it.smartcommunitylabdhub.commons.models.enums;

import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

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
        Assert.hasText(value, "value cannot be empty");
        this.value = value;
    }
}
