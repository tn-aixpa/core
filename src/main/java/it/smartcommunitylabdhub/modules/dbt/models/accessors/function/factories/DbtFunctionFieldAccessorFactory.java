package it.smartcommunitylabdhub.modules.dbt.models.accessors.function.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.modules.dbt.models.accessors.function.DbtFunctionFieldAccessor;
import org.springframework.stereotype.Component;

@Component
public class DbtFunctionFieldAccessorFactory implements AccessorFactory<DbtFunctionFieldAccessor> {
    @Override
    public DbtFunctionFieldAccessor create() {
        return new DbtFunctionFieldAccessor();
    }
}
