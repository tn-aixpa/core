package it.smartcommunitylabdhub.commons.models.entities.function;

import it.smartcommunitylabdhub.commons.jackson.JacksonMapper;
import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FunctionBaseSpec extends BaseSpec {

    private String source;

    @Override
    public void configure(Map<String, Object> data) {
        FunctionBaseSpec functionBaseSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
            data,
            FunctionBaseSpec.class
        );

        this.setSource(functionBaseSpec.getSource());
        super.configure(data);

        this.setExtraSpecs(functionBaseSpec.getExtraSpecs());
    }
}
