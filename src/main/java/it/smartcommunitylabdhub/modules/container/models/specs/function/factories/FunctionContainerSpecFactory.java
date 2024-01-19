package it.smartcommunitylabdhub.modules.container.models.specs.function.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.modules.container.models.specs.function.FunctionContainerSpec;
import org.springframework.stereotype.Component;

@Component
public class FunctionContainerSpecFactory implements SpecFactory<FunctionContainerSpec> {
    @Override
    public FunctionContainerSpec create() {
        return new FunctionContainerSpec();
    }
}
