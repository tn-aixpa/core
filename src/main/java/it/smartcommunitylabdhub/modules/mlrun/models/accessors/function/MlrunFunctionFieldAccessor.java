package it.smartcommunitylabdhub.modules.mlrun.models.accessors.function;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.FunctionFieldAccessor;

@AccessorType(kind = "mlrun", entity = EntityName.FUNCTION, factory = MlrunFunctionFieldAccessor.class)
public class MlrunFunctionFieldAccessor
        extends AbstractFieldAccessor<MlrunFunctionFieldAccessor>
        implements FunctionFieldAccessor<MlrunFunctionFieldAccessor> {

}
