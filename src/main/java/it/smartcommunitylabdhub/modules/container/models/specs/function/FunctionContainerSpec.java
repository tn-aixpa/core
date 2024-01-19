package it.smartcommunitylabdhub.modules.container.models.specs.function;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@SpecType(kind = "container", entity = EntityName.FUNCTION, factory = FunctionContainerSpec.class)
public class FunctionContainerSpec extends FunctionBaseSpec<FunctionContainerSpec> {

    private String handler;
    private String image;
    private String entrypoint;
    private List<Serializable> args;

    @Override
    protected void configureSpec(FunctionContainerSpec functionPythonSpec) {
        super.configureSpec(functionPythonSpec);

        this.setHandler(functionPythonSpec.getHandler());
        this.setImage(functionPythonSpec.getImage());
        this.setArgs(functionPythonSpec.getArgs());
        this.setEntrypoint(functionPythonSpec.getEntrypoint());
    }
}
