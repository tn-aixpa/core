package it.smartcommunitylabdhub.runtime.kfp.specs.function;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FunctionKFPSpecFactory implements SpecFactory<FunctionKFPSpec> {

    @Override
    public FunctionKFPSpec create() {
        return new FunctionKFPSpec();
    }

    @Override
    public FunctionKFPSpec create(Map<String, Serializable> data) {
        return new FunctionKFPSpec(data);
    }
}
