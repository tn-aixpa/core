package it.smartcommunitylabdhub.runtime.huggingface.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
@SpecType(kind = "huggingface", entity = EntityName.MODEL)
public class HuggingFaceModelSpec extends ModelBaseSpec {

    @JsonProperty("base_model")
    private String baseModel;

    @JsonProperty("parameters")
    private Map<String, Serializable> parameters = new LinkedHashMap<>();

    //Huggingface model id
    @JsonProperty("model_id")
    @Schema(title = "fields.huggingface.modelid.title", description = "fields.huggingface.modelid.description")
    private String modelId;

    //Huggingface model revision
    @JsonProperty("model_revision")
    @Schema(
        title = "fields.huggingface.modelrevision.title",
        description = "fields.huggingface.modelrevision.description"
    )
    private String modelRevision;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        HuggingFaceModelSpec spec = mapper.convertValue(data, HuggingFaceModelSpec.class);

        this.baseModel = spec.getBaseModel();
        this.parameters = spec.getParameters();
        this.modelId = spec.getModelId();
        this.modelRevision = spec.getModelRevision();
    }
}
