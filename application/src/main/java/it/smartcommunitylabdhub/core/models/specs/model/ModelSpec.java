package it.smartcommunitylabdhub.core.models.specs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "model", entity = EntityName.MODEL)
public class ModelSpec extends it.smartcommunitylabdhub.commons.models.model.ModelBaseSpec {

    @JsonProperty("parameters")
    private Map<String, Serializable> parameters = new LinkedHashMap<>();

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        ModelSpec spec = mapper.convertValue(data, ModelSpec.class);

        this.parameters = spec.getParameters();
    }
}
