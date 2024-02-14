package it.smartcommunitylabdhub.core.models.entities.run.specs.factories;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import it.smartcommunitylabdhub.core.models.entities.run.specs.RunRunSpec;
import org.springframework.stereotype.Component;

@Component
public class RunRunSpecFactory implements SpecFactory<RunRunSpec> {

    @Override
    public RunRunSpec create() {
        return new RunRunSpec();
    }
}
