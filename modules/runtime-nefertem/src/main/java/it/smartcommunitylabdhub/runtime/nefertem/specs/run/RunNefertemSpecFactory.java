package it.smartcommunitylabdhub.runtime.nefertem.specs.run;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class RunNefertemSpecFactory implements SpecFactory<RunNefertemSpec> {

    @Override
    public RunNefertemSpec create() {
        return RunNefertemSpec.builder().build();
    }
}
