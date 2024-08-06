package it.smartcommunitylabdhub.core.models.specs.model;


import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@SpecType(kind = "pickle", entity = EntityName.MODEL)
public class SKLearnModelSpec extends ModelSpec {

    @JsonProperty("runtime_version")
    private String runtimeVersion;


    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        SKLearnModelSpec spec = mapper.convertValue(data, SKLearnModelSpec.class);
        this.runtimeVersion = spec.getRuntimeVersion();
    }
}
