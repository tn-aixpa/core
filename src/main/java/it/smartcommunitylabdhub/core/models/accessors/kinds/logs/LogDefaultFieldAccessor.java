package it.smartcommunitylabdhub.core.models.accessors.kinds.logs;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.LogFieldAccessor;

@AccessorType(kind = "log", entity = EntityName.LOG)
public class LogDefaultFieldAccessor
        extends AbstractFieldAccessor<LogDefaultFieldAccessor>
        implements LogFieldAccessor<LogDefaultFieldAccessor> {

}
