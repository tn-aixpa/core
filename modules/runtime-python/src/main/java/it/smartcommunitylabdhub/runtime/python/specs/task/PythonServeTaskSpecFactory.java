package it.smartcommunitylabdhub.runtime.python.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PythonServeTaskSpecFactory implements SpecFactory<PythonServeTaskSpec> {

    @Override
    public PythonServeTaskSpec create() {
        return new PythonServeTaskSpec();
    }

    @Override
    public PythonServeTaskSpec create(Map<String, Serializable> data) {
        return new PythonServeTaskSpec(data);
    }
}
