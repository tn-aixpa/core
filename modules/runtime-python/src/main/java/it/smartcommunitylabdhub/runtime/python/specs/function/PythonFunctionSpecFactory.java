package it.smartcommunitylabdhub.runtime.python.specs.function;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PythonFunctionSpecFactory implements SpecFactory<PythonFunctionSpec> {

    @Override
    public PythonFunctionSpec create() {
        return new PythonFunctionSpec();
    }

    @Override
    public PythonFunctionSpec create(Map<String, Serializable> data) {
        return new PythonFunctionSpec(data);
    }
}
