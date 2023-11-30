package it.smartcommunitylabdhub.core.models.accessors.kinds.runs;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.RunFieldAccessor;


@AccessorType(kind = "run", entity = EntityName.RUN)
public class RunDefaultFieldAccessor
        extends AbstractFieldAccessor<RunDefaultFieldAccessor>
        implements RunFieldAccessor<RunDefaultFieldAccessor> {
}
