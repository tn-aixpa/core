package it.smartcommunitylabdhub.commons.models.entities.project;

import it.smartcommunitylabdhub.commons.models.base.BaseSpec;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ProjectBaseSpec extends BaseSpec {

    private String context;

    private List<Object> functions = new ArrayList<>();

    private List<Object> artifacts = new ArrayList<>();

    private List<Object> workflows = new ArrayList<>();

    private List<Object> dataitems = new ArrayList<>();

    @Override
    public void configure(Map<String, Serializable> data) {
        ProjectBaseSpec concreteSpec = mapper.convertValue(data, ProjectBaseSpec.class);

        this.setContext(concreteSpec.getContext());
        this.setFunctions(concreteSpec.getFunctions());
        this.setArtifacts(concreteSpec.getArtifacts());
        this.setDataitems(concreteSpec.getDataitems());
        this.setWorkflows(concreteSpec.getWorkflows());
    }
}
