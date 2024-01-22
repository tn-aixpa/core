package it.smartcommunitylabdhub.core.models.accessors.kinds.logs.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.core.models.accessors.kinds.logs.LogDefaultFieldAccessor;
import org.springframework.stereotype.Component;

@Component
public class LogDefaultFieldAccessorFactory implements AccessorFactory<LogDefaultFieldAccessor> {
    @Override
    public LogDefaultFieldAccessor create() {
        return new LogDefaultFieldAccessor();
    }
}
