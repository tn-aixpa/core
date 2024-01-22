package it.smartcommunitylabdhub.core.models.accessors.kinds.functions.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.core.models.accessors.kinds.functions.FunctionDefaultFieldAccessor;
import org.springframework.stereotype.Component;

@Component
public class FunctionDefaultFieldAccessorFactory implements AccessorFactory<FunctionDefaultFieldAccessor> {
    @Override
    public FunctionDefaultFieldAccessor create() {
        return new FunctionDefaultFieldAccessor();
    }
}
