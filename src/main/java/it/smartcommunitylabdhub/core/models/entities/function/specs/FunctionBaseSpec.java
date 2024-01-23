package it.smartcommunitylabdhub.core.models.entities.function.specs;

import it.smartcommunitylabdhub.core.models.base.specs.BaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class FunctionBaseSpec extends BaseSpec {

    private String source;

    @Override
    public void configure(Map<String, Object> data) {

        FunctionBaseSpec functionBaseSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, FunctionBaseSpec.class);

        this.setSource(functionBaseSpec.getSource());
        super.configure(data);

        this.setExtraSpecs(functionBaseSpec.getExtraSpecs());
    }
}
