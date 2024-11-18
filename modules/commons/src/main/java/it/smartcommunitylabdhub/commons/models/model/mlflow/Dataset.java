package it.smartcommunitylabdhub.commons.models.model.mlflow;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dataset {

    @Schema(title = "fields.mlflow.inputdatasetname.title", description = "fields.mlflow.inputdatasetname.description")
    private String name;
    @Schema(title = "fields.mlflow.inputdatasetdigest.title", description = "fields.mlflow.inputdatasetdigest.description")
    private String digest;
    @Schema(title = "fields.mlflow.inputdatasetprofile.title", description = "fields.mlflow.inputdatasetprofile.description")
    private String profile;
    @Schema(title = "fields.mlflow.inputdatasetschema.title", description = "fields.mlflow.inputdatasetschema.description")
    private String schema;
    @Schema(title = "fields.mlflow.inputdatasetsource.title", description = "fields.mlflow.inputdatasetsource.description")
    private String source;
    @JsonProperty("source_type")
    @Schema(title = "fields.mlflow.inputdatasetsourcetype.title", description = "fields.mlflow.inputdatasetsourcetype.description")
    private String sourceType;
}