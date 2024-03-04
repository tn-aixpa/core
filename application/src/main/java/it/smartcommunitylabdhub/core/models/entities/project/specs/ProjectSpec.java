package it.smartcommunitylabdhub.core.models.entities.project.specs;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.entities.project.ProjectBaseSpec;
import it.smartcommunitylabdhub.commons.models.enums.EntityName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SpecType(kind = "project", entity = EntityName.PROJECT)
public class ProjectSpec extends ProjectBaseSpec {

    private List<Object> functions = new ArrayList<>();

    private List<Object> artifacts = new ArrayList<>();

    private List<Object> workflows = new ArrayList<>();

    private List<Object> dataitems = new ArrayList<>();

    @Override
    public void configure(Map<String, Serializable> data) {
        ProjectSpec concreteSpec = mapper.convertValue(data, ProjectSpec.class);

        this.setFunctions(concreteSpec.getFunctions());
        this.setArtifacts(concreteSpec.getArtifacts());
        this.setDataitems(concreteSpec.getDataitems());
        this.setWorkflows(concreteSpec.getWorkflows());

        super.configure(data);
    }
}
