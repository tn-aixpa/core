package it.smartcommunitylabdhub.runtime.nefertem.specs.run;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RunNefertemSpecFactory implements SpecFactory<RunNefertemSpec> {

    @Override
    public RunNefertemSpec create() {
        return new RunNefertemSpec();
    }

    @Override
    public RunNefertemSpec create(Map<String, Serializable> data) {
        return new RunNefertemSpec(data);
    }
}
