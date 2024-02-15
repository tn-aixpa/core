package it.smartcommunitylabdhub.runtime.mlrun.specs.function;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.mlrun.MlrunRuntime;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = MlrunRuntime.RUNTIME, kind = "mlrun", entity = EntityName.FUNCTION)
public class FunctionMlrunSpec extends FunctionBaseSpec {

    private String image;
    private String tag;
    private String handler;
    private String command;
    private List<Serializable> requirements;

    public FunctionMlrunSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        FunctionMlrunSpec functionMlrunSpec = mapper.convertValue(data, FunctionMlrunSpec.class);

        this.setImage(functionMlrunSpec.getImage());
        this.setTag(functionMlrunSpec.getTag());
        this.setHandler(functionMlrunSpec.getHandler());
        this.setCommand(functionMlrunSpec.getCommand());
        this.setRequirements(functionMlrunSpec.getRequirements());
    }
}
