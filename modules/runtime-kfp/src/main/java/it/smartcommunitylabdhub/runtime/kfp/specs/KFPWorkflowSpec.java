package it.smartcommunitylabdhub.runtime.kfp.specs;

import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.jackson.annotations.JsonSchemaIgnore;
import it.smartcommunitylabdhub.commons.models.entities.workflow.WorkflowBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import it.smartcommunitylabdhub.commons.models.objects.SourceCode;
import it.smartcommunitylabdhub.runtime.kfp.KFPRuntime;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@SpecType(runtime = KFPRuntime.RUNTIME, kind = KFPRuntime.RUNTIME, entity = EntityName.WORKFLOW)
public class KFPWorkflowSpec extends WorkflowBaseSpec {

    @NotNull
    @Schema(title = "fields.sourceCode.title", description = "fields.sourceCode.description")
    private SourceCode<KFPSourceCodeLanguages> source;

    @Schema(title = "fields.container.image.title", description = "fields.container.image.description")
    private String image;

    @Schema(title = "fields.container.tag.title", description = "fields.container.tag.description")
    private String tag;

    @Schema(title = "fields.sourceCode.handler.title", description = "fields.sourceCode.handler.description")
    private String handler;

    @JsonSchemaIgnore
    private String workflow;

    public KFPWorkflowSpec(Map<String, Serializable> data) {
        configure(data);
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        KFPWorkflowSpec spec = mapper.convertValue(data, KFPWorkflowSpec.class);

        this.source = spec.getSource();
        this.image = spec.getImage();
        this.tag = spec.getTag();
        this.handler = spec.getHandler();
        this.workflow = spec.getWorkflow();
    }

    public enum KFPSourceCodeLanguages {
        python,
    }
}
