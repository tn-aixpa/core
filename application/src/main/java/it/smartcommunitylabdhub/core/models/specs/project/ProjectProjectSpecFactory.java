package it.smartcommunitylabdhub.core.models.specs.project;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ProjectProjectSpecFactory implements SpecFactory<ProjectSpec> {

    @Override
    public ProjectSpec create() {
        return new ProjectSpec();
    }

    @Override
    public ProjectSpec create(Map<String, Serializable> data) {
        ProjectSpec spec = new ProjectSpec();
        spec.configure(data);

        return spec;
    }
}
