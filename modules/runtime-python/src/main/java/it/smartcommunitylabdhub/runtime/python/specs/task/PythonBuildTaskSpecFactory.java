package it.smartcommunitylabdhub.runtime.python.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
public class PythonBuildTaskSpecFactory implements SpecFactory<PythonBuildTaskSpec> {

    @Override
    public PythonBuildTaskSpec create() {
        return new PythonBuildTaskSpec();
    }

    @Override
    public PythonBuildTaskSpec create(Map<String, Serializable> data) {
        return new PythonBuildTaskSpec(data);
    }
}
