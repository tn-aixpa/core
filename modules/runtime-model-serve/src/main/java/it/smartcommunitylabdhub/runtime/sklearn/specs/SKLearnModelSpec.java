package it.smartcommunitylabdhub.runtime.sklearn.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.model.ModelBaseSpec;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SpecType(kind = "sklearn", entity = EntityName.MODEL)
public class SKLearnModelSpec extends ModelBaseSpec {

    @JsonProperty("parameters")
    private Map<String, Serializable> parameters = new LinkedHashMap<>();

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        SKLearnModelSpec spec = mapper.convertValue(data, SKLearnModelSpec.class);

        this.parameters = spec.getParameters();
    }
}
