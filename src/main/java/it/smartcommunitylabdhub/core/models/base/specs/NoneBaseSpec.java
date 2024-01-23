package it.smartcommunitylabdhub.core.models.base.specs;


import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class NoneBaseSpec extends BaseSpec {

    @Override
    public void configure(Map<String, Object> data) {
        NoneBaseSpec noneBaseSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, NoneBaseSpec.class);

        this.setExtraSpecs(noneBaseSpec.getExtraSpecs());

        super.configure(data);

    }
}
