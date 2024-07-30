package it.smartcommunitylabdhub.core.models.specs.model;


import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@SpecType(kind = "mlflow", entity = EntityName.MODEL)
public class MlflowModelSpec extends ModelSpec {

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        MlflowModelSpec spec = mapper.convertValue(data, MlflowModelSpec.class);
    }
}
