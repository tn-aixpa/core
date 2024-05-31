package it.smartcommunitylabdhub.runtime.python.specs.run;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PythonRunSpecFactory implements SpecFactory<PythonRunSpec> {

    @Override
    public PythonRunSpec create() {
        return new PythonRunSpec();
    }

    @Override
    public PythonRunSpec create(Map<String, Serializable> data) {
        return new PythonRunSpec(data);
    }
}
