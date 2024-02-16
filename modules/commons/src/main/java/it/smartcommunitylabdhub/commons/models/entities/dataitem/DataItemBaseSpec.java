package it.smartcommunitylabdhub.commons.models.entities.dataitem;

import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DataItemBaseSpec extends BaseSpec {

    private String key;
    private String path;

    @Override
    public void configure(Map<String, Serializable> data) {
        DataItemBaseSpec concreteSpec = mapper.convertValue(data, DataItemBaseSpec.class);

        this.setKey(concreteSpec.getKey());
        this.setPath(concreteSpec.getPath());
    }
}
