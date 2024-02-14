package it.smartcommunitylabdhub.runtime.dbt.models.specs.run.factories;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import it.smartcommunitylabdhub.runtime.dbt.models.specs.run.RunDbtSpec;
import org.springframework.stereotype.Component;

@Component
public class RunDbtSpecFactory implements SpecFactory<RunDbtSpec> {

    @Override
    public RunDbtSpec create() {
        return RunDbtSpec.builder().build();
    }
}
