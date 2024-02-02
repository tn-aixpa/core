package it.smartcommunitylabdhub.core.models.accessors.kinds.secrets;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.SecretFieldAccessor;


@AccessorType(kind = "secret", entity = EntityName.SECRET, factory = SecretDefaultFieldAccessor.class)
public class SecretDefaultFieldAccessor
        extends AbstractFieldAccessor<SecretDefaultFieldAccessor>
        implements SecretFieldAccessor<SecretDefaultFieldAccessor> {
}
