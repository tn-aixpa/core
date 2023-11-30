package it.smartcommunitylabdhub.modules.mlrun.components.accessors;

import it.smartcommunitylabdhub.core.annotations.common.AccessorType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.accessors.AbstractFieldAccessor;
import it.smartcommunitylabdhub.core.models.accessors.kinds.interfaces.FunctionFieldAccessor;

@AccessorType(kind = "job", entity = EntityName.FUNCTION)
public class JobFunctionFieldAccessor
        extends AbstractFieldAccessor<JobFunctionFieldAccessor>
        implements FunctionFieldAccessor<JobFunctionFieldAccessor> {


    // get code origin
    public String getCodeOrigin() {
        return mapHasField(getBuild(), "code_origin") ? (String) getBuild().get("code_origin") : null;
    }

    public String getOriginFilename() {
        return mapHasField(getBuild(), "origin_filename") ? (String) getBuild().get("origin_filename") : null;
    }

}
