package it.smartcommunitylabdhub.core.models.entities.function.specs;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@SpecType(kind = "python", entity = EntityName.FUNCTION)
public class FunctionPythonSpec extends FunctionBaseSpec<FunctionPythonSpec> {

    private String handler;
    private String image;
    private String command;
    private List<Object> args;
    private List<Object> requirements;

    @Override
    protected void configureSpec(FunctionPythonSpec functionPythonSpec) {
        super.configureSpec(functionPythonSpec);

        this.setHandler(functionPythonSpec.getHandler());
        this.setImage(functionPythonSpec.getImage());
        this.setArgs(functionPythonSpec.getArgs());
        this.setRequirements(functionPythonSpec.getRequirements());
    }
}
