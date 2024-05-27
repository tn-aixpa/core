package it.smartcommunitylabdhub.runtime.python.specs.function;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
public class FunctionPythonSpecFactory implements SpecFactory<FunctionPythonSpec> {

    @Override
    public FunctionPythonSpec create() {
        return new FunctionPythonSpec();
    }

    @Override
    public FunctionPythonSpec create(Map<String, Serializable> data) {
        return new FunctionPythonSpec(data);
    }
}
