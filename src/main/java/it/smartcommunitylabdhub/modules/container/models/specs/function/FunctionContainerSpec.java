package it.smartcommunitylabdhub.modules.container.models.specs.function;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.models.entities.function.specs.FunctionBaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@SpecType(kind = "container", entity = EntityName.FUNCTION, factory = FunctionContainerSpec.class)
public class FunctionContainerSpec extends FunctionBaseSpec {

    private String handler;
    private String image;
    private String entrypoint;
    private List<Serializable> args;

    @Override
    public void configure(Map<String, Object> data) {


        FunctionContainerSpec functionPythonSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, FunctionContainerSpec.class);

        this.setHandler(functionPythonSpec.getHandler());
        this.setImage(functionPythonSpec.getImage());
        this.setArgs(functionPythonSpec.getArgs());
        this.setEntrypoint(functionPythonSpec.getEntrypoint());
        super.configure(data);

        this.setExtraSpecs(functionPythonSpec.getExtraSpecs());
    }
}
