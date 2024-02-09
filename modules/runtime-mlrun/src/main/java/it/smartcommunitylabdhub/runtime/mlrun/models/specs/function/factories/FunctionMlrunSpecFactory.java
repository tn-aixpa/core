package it.smartcommunitylabdhub.modules.mlrun.models.specs.function.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.modules.mlrun.models.specs.function.FunctionMlrunSpec;
import org.springframework.stereotype.Component;

@Component
public class FunctionMlrunSpecFactory implements SpecFactory<FunctionMlrunSpec> {
    @Override
    public FunctionMlrunSpec create() {
        return new FunctionMlrunSpec();
    }
}
