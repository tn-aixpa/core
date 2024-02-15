package it.smartcommunitylabdhub.runtime.dbt.specs.function;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FunctionDbtSpecFactory implements SpecFactory<FunctionDbtSpec> {

    @Override
    public FunctionDbtSpec create() {
        return new FunctionDbtSpec();
    }

    @Override
    public FunctionDbtSpec create(Map<String, Serializable> data) {
        return new FunctionDbtSpec(data);
    }
}
