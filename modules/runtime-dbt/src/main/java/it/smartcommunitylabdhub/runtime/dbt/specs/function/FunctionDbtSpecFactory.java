package it.smartcommunitylabdhub.runtime.dbt.specs.function;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class FunctionDbtSpecFactory implements SpecFactory<FunctionDbtSpec> {

    @Override
    public FunctionDbtSpec create() {
        return new FunctionDbtSpec();
    }
}
