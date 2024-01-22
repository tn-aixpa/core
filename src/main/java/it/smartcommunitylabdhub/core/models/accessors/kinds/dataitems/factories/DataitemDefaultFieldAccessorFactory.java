package it.smartcommunitylabdhub.core.models.accessors.kinds.dataitems.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.core.models.accessors.kinds.dataitems.DataitemDefaultFieldAccessor;
import org.springframework.stereotype.Component;

@Component
public class DataitemDefaultFieldAccessorFactory implements AccessorFactory<DataitemDefaultFieldAccessor> {
    @Override
    public DataitemDefaultFieldAccessor create() {
        return new DataitemDefaultFieldAccessor();
    }
}
