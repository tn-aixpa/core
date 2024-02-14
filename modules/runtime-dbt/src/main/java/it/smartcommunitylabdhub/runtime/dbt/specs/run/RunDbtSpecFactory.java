package it.smartcommunitylabdhub.runtime.dbt.specs.run;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class RunDbtSpecFactory implements SpecFactory<RunDbtSpec> {

    @Override
    public RunDbtSpec create() {
        return RunDbtSpec.builder().build();
    }
}
