package it.smartcommunitylabdhub.runtime.nefertem.models.accessors.function;

import it.smartcommunitylabdhub.commons.annotations.common.AccessorType;
import it.smartcommunitylabdhub.commons.models.accessors.kinds.interfaces.FunctionFieldAccessor;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;

@AccessorType(kind = "nefertem", entity = EntityName.FUNCTION, factory = NefertemFunctionFieldAccessor.class)
public class NefertemFunctionFieldAccessor
        extends AbstractFieldAccessor<NefertemFunctionFieldAccessor>
        implements FunctionFieldAccessor<NefertemFunctionFieldAccessor> {

}
