package it.smartcommunitylabdhub.core.models.accessors.kinds.secrets.factories;

import it.smartcommunitylabdhub.core.components.infrastructure.factories.accessors.AccessorFactory;
import it.smartcommunitylabdhub.core.models.accessors.kinds.secrets.SecretDefaultFieldAccessor;

import org.springframework.stereotype.Component;

@Component
public class SecretDefaultFieldAccessorFactory implements AccessorFactory<SecretDefaultFieldAccessor> {
    @Override
    public SecretDefaultFieldAccessor create() {
        return new SecretDefaultFieldAccessor();
    }
}
