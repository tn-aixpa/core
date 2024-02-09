package it.smartcommunitylabdhub.core.models.accessors.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskAccessor {
    private String runtime;
    private String project;
    private String name;
    private String version;

    public TaskAccessor(String runtime, String project, String function, String version) {
        this.runtime = runtime;
        this.project = project;
        this.name = function;
        this.version = version;
    }
}
