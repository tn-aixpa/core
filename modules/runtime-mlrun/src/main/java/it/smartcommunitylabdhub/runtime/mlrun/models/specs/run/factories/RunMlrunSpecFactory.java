package it.smartcommunitylabdhub.modules.mlrun.models.specs.run.factories;


import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.modules.mlrun.models.specs.run.RunMlrunSpec;
import org.springframework.stereotype.Component;

@Component
public class RunMlrunSpecFactory implements SpecFactory<RunMlrunSpec> {
    @Override
    public RunMlrunSpec create() {
        return RunMlrunSpec.builder().build();
    }
}
