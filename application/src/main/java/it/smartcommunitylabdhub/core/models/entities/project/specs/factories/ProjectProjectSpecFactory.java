package it.smartcommunitylabdhub.core.models.entities.project.specs.factories;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import it.smartcommunitylabdhub.core.models.entities.project.specs.ProjectProjectSpec;
import org.springframework.stereotype.Component;

@Component
public class ProjectProjectSpecFactory implements SpecFactory<ProjectProjectSpec> {

    @Override
    public ProjectProjectSpec create() {
        return new ProjectProjectSpec();
    }
}
