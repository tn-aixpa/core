package it.smartcommunitylabdhub.runtime.mlrun.specs.run;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import java.io.Serializable;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class RunMlrunSpecFactory implements SpecFactory<RunMlrunSpec> {

    @Override
    public RunMlrunSpec create() {
        return new RunMlrunSpec();
    }

    @Override
    public RunMlrunSpec create(Map<String, Serializable> data) {
        return new RunMlrunSpec(data);
    }
}
