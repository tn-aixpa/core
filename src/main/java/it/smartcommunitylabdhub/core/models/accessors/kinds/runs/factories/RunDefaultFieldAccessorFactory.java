package it.smartcommunitylabdhub.core.models.accessors.kinds.runs.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.core.models.accessors.kinds.runs.RunDefaultFieldAccessor;
import org.springframework.stereotype.Component;

@Component
public class RunDefaultFieldAccessorFactory implements AccessorFactory<RunDefaultFieldAccessor> {
    @Override
    public RunDefaultFieldAccessor create() {
        return new RunDefaultFieldAccessor();
    }
}
