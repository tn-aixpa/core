package it.smartcommunitylabdhub.runtime.mlrun.models.specs.function;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.entities.function.FunctionBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "mlrun", entity = EntityName.FUNCTION)
public class FunctionMlrunSpec extends FunctionBaseSpec {

    private String image;
    private String tag;
    private String handler;
    private String command;
    private List<Serializable> requirements;

    @Override
    public void configure(Map<String, Object> data) {
        FunctionMlrunSpec functionMlrunSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
            data,
            FunctionMlrunSpec.class
        );

        this.setImage(functionMlrunSpec.getImage());
        this.setTag(functionMlrunSpec.getTag());
        this.setHandler(functionMlrunSpec.getHandler());
        this.setCommand(functionMlrunSpec.getCommand());
        this.setRequirements(functionMlrunSpec.getRequirements());
        super.configure(data);

        this.setExtraSpecs(functionMlrunSpec.getExtraSpecs());
    }
}
