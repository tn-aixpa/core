package it.smartcommunitylabdhub.runtime.nefertem.specs.function;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class FunctionNefertemSpecFactory implements SpecFactory<FunctionNefertemSpec> {

    @Override
    public FunctionNefertemSpec create() {
        return new FunctionNefertemSpec();
    }
}
