package it.smartcommunitylabdhub.modules.mlrun.models.accessors.function.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.modules.mlrun.models.accessors.function.MlrunFunctionFieldAccessor;
import org.springframework.stereotype.Component;

@Component
public class MlrunFunctionFieldAccessorFactory implements AccessorFactory<MlrunFunctionFieldAccessor> {
    @Override
    public MlrunFunctionFieldAccessor create() {
        return new MlrunFunctionFieldAccessor();
    }
}
