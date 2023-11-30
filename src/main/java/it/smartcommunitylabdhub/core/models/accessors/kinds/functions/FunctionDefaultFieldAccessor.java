package it.smartcommunitylabdhub.core.models.accessors.kinds.functions;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.FunctionFieldAccessor;

@AccessorType(kind = "function", entity = EntityName.FUNCTION)
public class FunctionDefaultFieldAccessor
        extends AbstractFieldAccessor<FunctionDefaultFieldAccessor>
        implements FunctionFieldAccessor<FunctionDefaultFieldAccessor> {


    // get code origin
    public String getCodeOrigin() {
        return mapHasField(getBuild(), "code_origin") ? (String) getBuild().get("code_origin") : null;
    }

    public String getOriginFilename() {
        return mapHasField(getBuild(), "origin_filename") ? (String) getBuild().get("origin_filename") : null;
    }

}
