package it.smartcommunitylabdhub.commons.models.project;

import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectBaseSpec extends BaseSpec {

    private String source;
    private ProjectConfig config = new ProjectConfig();

    @Override
    public void configure(Map<String, Serializable> data) {
        ProjectBaseSpec spec = mapper.convertValue(data, ProjectBaseSpec.class);

        this.config = spec.getConfig();
        this.source = spec.getSource();
    }
}
