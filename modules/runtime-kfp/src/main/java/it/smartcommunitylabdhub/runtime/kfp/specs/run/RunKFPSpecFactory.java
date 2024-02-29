package it.smartcommunitylabdhub.runtime.kfp.specs.run;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RunKFPSpecFactory implements SpecFactory<RunKFPSpec> {

    @Override
    public RunKFPSpec create() {
        return new RunKFPSpec();
    }

    @Override
    public RunKFPSpec create(Map<String, Serializable> data) {
        return new RunKFPSpec(data);
    }
}
