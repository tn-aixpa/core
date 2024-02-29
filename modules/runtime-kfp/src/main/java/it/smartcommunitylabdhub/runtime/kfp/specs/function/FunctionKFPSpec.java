package it.smartcommunitylabdhub.runtime.kfp.specs.function;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.runtime.kfp.KFPRuntime;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = KFPRuntime.RUNTIME, kind = "kfp", entity = EntityName.FUNCTION)
public class FunctionKFPSpec extends FunctionBaseSpec {

    private String image;
    private String tag;
    private String handler;
    private String command;
    private List<Serializable> requirements;
    private Map<String, Serializable> build;

    public FunctionKFPSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        FunctionKFPSpec functionKFPSpec = mapper.convertValue(data, FunctionKFPSpec.class);

        this.setImage(functionKFPSpec.getImage());
        this.setTag(functionKFPSpec.getTag());
        this.setHandler(functionKFPSpec.getHandler());
        this.setCommand(functionKFPSpec.getCommand());
        this.setRequirements(functionKFPSpec.getRequirements());
        this.setBuild(functionKFPSpec.getBuild());
    }
}
