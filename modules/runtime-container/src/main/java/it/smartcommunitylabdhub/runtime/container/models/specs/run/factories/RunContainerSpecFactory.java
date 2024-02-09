package it.smartcommunitylabdhub.runtime.container.models.specs.run.factories;


import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.runtime.container.models.specs.run.RunContainerSpec;

import org.springframework.stereotype.Component;

@Component
public class RunContainerSpecFactory implements SpecFactory<RunContainerSpec> {
    @Override
    public RunContainerSpec create() {
        return RunContainerSpec.builder().build();
    }
}
