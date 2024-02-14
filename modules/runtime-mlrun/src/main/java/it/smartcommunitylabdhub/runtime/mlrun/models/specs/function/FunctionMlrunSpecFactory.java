package it.smartcommunitylabdhub.runtime.mlrun.models.specs.function;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class FunctionMlrunSpecFactory implements SpecFactory<FunctionMlrunSpec> {

    @Override
    public FunctionMlrunSpec create() {
        return new FunctionMlrunSpec();
    }
}
