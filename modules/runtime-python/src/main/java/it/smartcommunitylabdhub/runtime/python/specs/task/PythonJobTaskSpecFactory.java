package it.smartcommunitylabdhub.runtime.python.specs.task;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PythonJobTaskSpecFactory implements SpecFactory<PythonJobTaskSpec> {

    @Override
    public PythonJobTaskSpec create() {
        return new PythonJobTaskSpec();
    }

    @Override
    public PythonJobTaskSpec create(Map<String, Serializable> data) {
        return new PythonJobTaskSpec(data);
    }
}
