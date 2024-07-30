package it.smartcommunitylabdhub.core.models.specs.model;


import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@SpecType(kind = "pickle", entity = EntityName.MODEL)
public class PickleModelSpec extends ModelSpec {

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        PickleModelSpec spec = mapper.convertValue(data, PickleModelSpec.class);
    }
}
