package it.smartcommunitylabdhub.commons.models.entities.model;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.models.entities.model.mlflow.Dataset;
import it.smartcommunitylabdhub.commons.models.entities.model.mlflow.Signature;

@Getter
@Setter
public class MlflowModelSpec extends ModelSpec {

    @Schema(title = "fields.mlflow.flavor.title", description = "fields.mlflow.flavor.description")
    private String flavor;
    
    @JsonProperty("model_config")
    @Schema(title = "fields.mlflow.modelconfig.title", description = "fields.mlflow.modelconfig.description")
    private Map<String, String> modelConfig;

    @JsonProperty("input_datasets")
    @Schema(title = "fields.mlflow.inputdatasets.title", description = "fields.mlflow.inputdatasets.description")
    private List<Dataset> inputDatasets;

    @Schema(title = "fields.mlflow.signature.title", description = "fields.mlflow.signature.description")
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