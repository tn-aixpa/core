package it.smartcommunitylabdhub.modules.dbt.models.specs.function.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.modules.dbt.models.specs.function.FunctionDbtSpec;
import org.springframework.stereotype.Component;

@Component
public class FunctionDbtSpecFactory implements SpecFactory<FunctionDbtSpec> {
    @Override
    public FunctionDbtSpec create() {
        return new FunctionDbtSpec();
    }
}
