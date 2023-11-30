package it.smartcommunitylabdhub.modules.mlrun.components.accessors;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.FunctionFieldAccessor;

@AccessorType(kind = "serving", entity = EntityName.FUNCTION)
public class ServingFunctionFieldAccessor
        extends AbstractFieldAccessor<ServingFunctionFieldAccessor>
        implements FunctionFieldAccessor<ServingFunctionFieldAccessor> {

}
