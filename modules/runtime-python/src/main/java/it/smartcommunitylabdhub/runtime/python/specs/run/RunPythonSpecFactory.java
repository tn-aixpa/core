package it.smartcommunitylabdhub.runtime.python.specs.run;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
public class RunPythonSpecFactory implements SpecFactory<RunPythonSpec> {

    @Override
    public RunPythonSpec create() {
        return new RunPythonSpec();
    }

    @Override
    public RunPythonSpec create(Map<String, Serializable> data) {
        return new RunPythonSpec(data);
    }
}
