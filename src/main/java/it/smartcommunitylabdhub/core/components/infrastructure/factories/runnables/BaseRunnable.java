package it.smartcommunitylabdhub.core.components.infrastructure.factories.runnables;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseRunnable implements Runnable {
    String id;
    String project;
    String framework;

    @Override
    public String getFramework() {
        return this.framework;
    }

    @Override
    public String getProject() {
        return this.project;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
