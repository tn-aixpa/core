package it.smartcommunitylabdhub.runtime.mlrun.specs.function;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FunctionMlrunSpecFactory implements SpecFactory<FunctionMlrunSpec> {

    @Override
    public FunctionMlrunSpec create() {
        return new FunctionMlrunSpec();
    }

    @Override
    public FunctionMlrunSpec create(Map<String, Serializable> data) {
        return new FunctionMlrunSpec(data);
    }
}
