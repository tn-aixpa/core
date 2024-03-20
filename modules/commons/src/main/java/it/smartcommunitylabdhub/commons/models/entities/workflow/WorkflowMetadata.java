package it.smartcommunitylabdhub.commons.models.entities.workflow;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.base.BaseMetadata;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkflowMetadata extends BaseMetadata {

    private String version;

    private Boolean embedded;

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        WorkflowMetadata meta = mapper.convertValue(data, WorkflowMetadata.class);

        this.version = meta.getVersion();
        this.embedded = meta.getEmbedded();
    }
}
