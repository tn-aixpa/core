package it.smartcommunitylabdhub.core.models.entities.function.specs;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SpecType(kind = "mlrun", entity = EntityName.FUNCTION)
public class FunctionMlrunSpec extends FunctionBaseSpec<FunctionMlrunSpec> {
    private String image;
    private String tag;
    private String handler;
    private String command;
    private List<Object> requirements;

    @Override
    protected void configureSpec(FunctionMlrunSpec functionMlrunSpec) {
        super.configureSpec(functionMlrunSpec);

        this.setImage(functionMlrunSpec.getImage());
        this.setTag(functionMlrunSpec.getTag());
        this.setHandler(functionMlrunSpec.getHandler());
        this.setCommand(functionMlrunSpec.getCommand());
        this.setRequirements(functionMlrunSpec.getRequirements());
    }
}
