package it.smartcommunitylabdhub.core.models.entities.project.specs.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.core.models.entities.project.specs.ProjectProjectSpec;
import org.springframework.stereotype.Component;

@Component
public class ProjectProjectSpecFactory implements SpecFactory<ProjectProjectSpec> {
    @Override
    public ProjectProjectSpec create() {
        return new ProjectProjectSpec();
    }
}
