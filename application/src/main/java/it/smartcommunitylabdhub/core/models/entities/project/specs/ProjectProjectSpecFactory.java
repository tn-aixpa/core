package it.smartcommunitylabdhub.core.models.entities.project.specs;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ProjectProjectSpecFactory implements SpecFactory<ProjectProjectSpec> {

    @Override
    public ProjectProjectSpec create() {
        return new ProjectProjectSpec();
    }

    @Override
    public ProjectProjectSpec create(Map<String, Serializable> data) {
        ProjectProjectSpec spec = new ProjectProjectSpec();
        spec.configure(data);

        return spec;
    }
}
