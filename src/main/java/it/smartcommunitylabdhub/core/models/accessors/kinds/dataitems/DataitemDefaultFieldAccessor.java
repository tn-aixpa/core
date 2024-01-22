package it.smartcommunitylabdhub.core.models.accessors.kinds.dataitems;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.DataItemFieldAccessor;

@AccessorType(kind = "dataitem", entity = EntityName.DATAITEM, factory = DataitemDefaultFieldAccessor.class)
public class DataitemDefaultFieldAccessor
        extends AbstractFieldAccessor<DataitemDefaultFieldAccessor>
        implements DataItemFieldAccessor<DataitemDefaultFieldAccessor> {

}
