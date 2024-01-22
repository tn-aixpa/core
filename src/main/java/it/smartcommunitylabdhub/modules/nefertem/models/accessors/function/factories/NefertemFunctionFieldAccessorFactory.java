package it.smartcommunitylabdhub.modules.nefertem.models.accessors.function.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.modules.nefertem.models.accessors.function.NefertemFunctionFieldAccessor;
import org.springframework.stereotype.Component;

@Component
public class NefertemFunctionFieldAccessorFactory implements AccessorFactory<NefertemFunctionFieldAccessor> {
    @Override
    public NefertemFunctionFieldAccessor create() {
        return new NefertemFunctionFieldAccessor();
    }
}
