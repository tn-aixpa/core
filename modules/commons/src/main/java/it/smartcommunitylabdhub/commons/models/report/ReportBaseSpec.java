package it.smartcommunitylabdhub.commons.models.report;

import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportBaseSpec extends BaseSpec {

    @NotEmpty
    private String entity;

    @NotEmpty
    @JsonProperty("entity_type")
    private String entityType;

    @JsonProperty("framework")
    private String framework;

    @NotBlank
    private String path;

    @JsonProperty("src_path")
    private String srcPath;

    @Override
    public void configure(Map<String, Serializable> data) {
        ReportBaseSpec spec = mapper.convertValue(data, ReportBaseSpec.class);

        this.entity = spec.getEntity();
        this.entityType = spec.getEntityType();
        this.path = spec.getPath();
        this.srcPath = spec.getSrcPath();
        this.framework = spec.getFramework();
    }
}
