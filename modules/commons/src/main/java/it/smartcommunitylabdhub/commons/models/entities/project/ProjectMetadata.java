package it.smartcommunitylabdhub.commons.models.entities.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.base.BaseMetadata;
import java.io.Serializable;
import java.util.Date;
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
public class ProjectMetadata extends BaseMetadata {

    private String source;

    @Builder
    public ProjectMetadata(
        String project,
        String name,
        String description,
        Date created,
        Date updated,
        Set<String> labels,
        String source
    ) {
        super(project, name, description, created, updated, labels);
        this.source = source;
    }

    @Override
    public void configure(Map<String, Serializable> data) {
        super.configure(data);

        ProjectMetadata meta = mapper.convertValue(data, ProjectMetadata.class);

        this.source = meta.getSource();
    }
}
