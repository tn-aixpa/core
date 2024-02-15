package it.smartcommunitylabdhub.runtime.nefertem.specs.function;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FunctionNefertemSpecFactory implements SpecFactory<FunctionNefertemSpec> {

    @Override
    public FunctionNefertemSpec create() {
        return new FunctionNefertemSpec();
    }

    @Override
    public FunctionNefertemSpec create(Map<String, Serializable> data) {
        return new FunctionNefertemSpec(data);
    }
}
