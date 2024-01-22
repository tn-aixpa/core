package it.smartcommunitylabdhub.modules.nefertem.models.specs.function.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.function.FunctionNefertemSpec;
import org.springframework.stereotype.Component;

@Component
public class FunctionNefertemSpecFactory implements SpecFactory<FunctionNefertemSpec> {
    @Override
    public FunctionNefertemSpec create() {
        return new FunctionNefertemSpec();
    }
}
