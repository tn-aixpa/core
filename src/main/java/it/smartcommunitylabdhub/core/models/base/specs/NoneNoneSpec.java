package it.smartcommunitylabdhub.core.models.base.specs;


import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
@SpecType(kind = "none", entity = EntityName.NONE, factory = NoneNoneSpec.class)
public class NoneNoneSpec extends NoneBaseSpec {
    @Override
    public void configure(Map<String, Object> data) {
        NoneNoneSpec noneNoneSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, NoneNoneSpec.class);

        super.configure(data);
        
        this.setExtraSpecs(noneNoneSpec.getExtraSpecs());

    }
}
