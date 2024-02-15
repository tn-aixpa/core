package it.smartcommunitylabdhub.runtime.container.specs.function;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FunctionContainerSpecFactory implements SpecFactory<FunctionContainerSpec> {

    @Override
    public FunctionContainerSpec create() {
        return new FunctionContainerSpec();
    }

    @Override
    public FunctionContainerSpec create(Map<String, Serializable> data) {
        return new FunctionContainerSpec(data);
    }
}
