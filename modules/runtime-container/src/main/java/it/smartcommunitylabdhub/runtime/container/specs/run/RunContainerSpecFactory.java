package it.smartcommunitylabdhub.runtime.container.specs.run;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class RunContainerSpecFactory implements SpecFactory<RunContainerSpec> {

    @Override
    public RunContainerSpec create() {
        return RunContainerSpec.builder().build();
    }
}
