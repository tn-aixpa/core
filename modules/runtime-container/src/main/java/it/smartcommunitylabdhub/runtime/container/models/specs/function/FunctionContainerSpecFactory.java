package it.smartcommunitylabdhub.runtime.container.models.specs.function;

import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class FunctionContainerSpecFactory implements SpecFactory<FunctionContainerSpec> {

    @Override
    public FunctionContainerSpec create() {
        return new FunctionContainerSpec();
    }
}
