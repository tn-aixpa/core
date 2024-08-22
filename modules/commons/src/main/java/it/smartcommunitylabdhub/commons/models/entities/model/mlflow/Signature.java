package it.smartcommunitylabdhub.commons.models.entities.model.mlflow;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Signature {

    @Schema(title = "fields.mlflow.signatureinputs.title", description = "fields.mlflow.signatureinputs.description")
    private String inputs;
    @Schema(title = "fields.mlflow.signatureoutputs.title", description = "fields.mlflow.signatureoutputs.description")
    private String outputs;
    @Schema(title = "fields.mlflow.signatureparams.title", description = "fields.mlflow.signatureparams.description")
    private String params;
}