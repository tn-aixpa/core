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
@SpecType(kind = "huggingface", entity = EntityName.MODEL)
public class HuggingFaceModelSpec extends ModelSpec {

    //Huggingface model id
    @JsonProperty("model_id")
    private String modelId;

    //Huggingface model revision
    @JsonProperty("model_revision")
    private String modelRevision;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        HuggingFaceModelSpec spec = mapper.convertValue(data, HuggingFaceModelSpec.class);

        this.modelId = spec.getModelId();
        this.modelRevision = spec.getModelRevision();
    }
}
