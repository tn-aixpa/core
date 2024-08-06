package it.smartcommunitylabdhub.core.models.specs.model;


import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.core.models.specs.model.mlflow.Dataset;
import it.smartcommunitylabdhub.core.models.specs.model.mlflow.Signature;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@SpecType(kind = "mlflow", entity = EntityName.MODEL)
public class MlflowModelSpec extends ModelSpec {

    private String flavor;
    
    @JsonProperty("model_config")
    private Map<String, String> modelConfig;

    @JsonProperty("input_datasets")
    private List<Dataset> inputDatasets;

    private Signature signature;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        MlflowModelSpec spec = mapper.convertValue(data, MlflowModelSpec.class);
        this.flavor = spec.getFlavor();
        this.signature = spec.getSignature();
        this.inputDatasets = spec.getInputDatasets();
        this.modelConfig = spec.getModelConfig();
    }
}