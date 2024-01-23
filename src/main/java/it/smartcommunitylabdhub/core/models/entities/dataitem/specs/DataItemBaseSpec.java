package it.smartcommunitylabdhub.core.models.entities.dataitem.specs;

import it.smartcommunitylabdhub.core.models.base.specs.BaseSpec;
import it.smartcommunitylabdhub.core.utils.jackson.JacksonMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;


@Getter
@Setter
public class DataItemBaseSpec extends BaseSpec {
    private String key;
    private String path;

    @Override
    public void configure(Map<String, Object> data) {

        DataItemBaseSpec concreteSpec = JacksonMapper.CUSTOM_OBJECT_MAPPER.convertValue(
                data, DataItemBaseSpec.class);
        this.setKey(concreteSpec.getKey());
        this.setPath(concreteSpec.getPath());

        super.configure(data);

        this.setExtraSpecs(concreteSpec.getExtraSpecs());


    }
}
