package it.smartcommunitylabdhub.modules.dbt.models.specs.run.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.modules.dbt.models.specs.run.RunDbtSpec;
import org.springframework.stereotype.Component;

@Component
public class RunDbtSpecFactory implements SpecFactory<RunDbtSpec> {
    @Override
    public RunDbtSpec create() {
        return RunDbtSpec
                .builder()
                .build();
    }
}
