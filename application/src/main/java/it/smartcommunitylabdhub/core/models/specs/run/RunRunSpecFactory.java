package it.smartcommunitylabdhub.core.models.specs.run;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RunRunSpecFactory implements SpecFactory<RunRunSpec> {

    @Override
    public RunRunSpec create() {
        return new RunRunSpec();
    }

    @Override
    public RunRunSpec create(Map<String, Serializable> data) {
        RunRunSpec spec = new RunRunSpec();
        spec.configure(data);

        return spec;
    }
}
