package it.smartcommunitylabdhub.modules.nefertem.models.specs.run.factories;


import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.core.models.entities.task.specs.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.modules.nefertem.models.specs.run.RunNefertemSpec;
import org.springframework.stereotype.Component;

@Component
public class RunNefertemSpecFactory implements SpecFactory<RunNefertemSpec<? extends K8sTaskBaseSpec>> {
    @Override
    public RunNefertemSpec<?> create() {
        return RunNefertemSpec.builder().build();
    }
}
