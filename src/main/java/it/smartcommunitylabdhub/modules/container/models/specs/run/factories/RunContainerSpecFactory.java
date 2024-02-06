package it.smartcommunitylabdhub.modules.container.models.specs.run.factories;


import it.smartcommunitylabdhub.core.components.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.core.models.entities.task.specs.K8sTaskBaseSpec;
import it.smartcommunitylabdhub.modules.container.models.specs.run.RunContainerSpec;
import org.springframework.stereotype.Component;

@Component
public class RunContainerSpecFactory implements SpecFactory<RunContainerSpec<? extends K8sTaskBaseSpec>> {
    @Override
    public RunContainerSpec<? extends K8sTaskBaseSpec> create() {
        return RunContainerSpec.builder().build();
    }
}
