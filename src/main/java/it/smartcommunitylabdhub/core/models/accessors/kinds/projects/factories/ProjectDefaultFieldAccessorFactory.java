package it.smartcommunitylabdhub.core.models.accessors.kinds.projects.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.core.models.accessors.kinds.projects.ProjectDefaultFieldAccessor;
import org.springframework.stereotype.Component;

@Component
public class ProjectDefaultFieldAccessorFactory implements AccessorFactory<ProjectDefaultFieldAccessor> {
    @Override
    public ProjectDefaultFieldAccessor create() {
        return new ProjectDefaultFieldAccessor();
    }
}
