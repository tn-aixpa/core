package it.smartcommunitylabdhub.runtime.container.specs.run;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RunContainerSpecFactory implements SpecFactory<RunContainerSpec> {

    @Override
    public RunContainerSpec create() {
        return new RunContainerSpec();
    }

    @Override
    public RunContainerSpec create(Map<String, Serializable> data) {
        return new RunContainerSpec(data);
    }
}
