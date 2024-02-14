package it.smartcommunitylabdhub.runtime.mlrun.specs.run;

import it.smartcommunitylabdhub.commons.infrastructure.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class RunMlrunSpecFactory implements SpecFactory<RunMlrunSpec> {

    @Override
    public RunMlrunSpec create() {
        return RunMlrunSpec.builder().build();
    }
}
