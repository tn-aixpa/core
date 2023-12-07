package it.smartcommunitylabdhub.core.models.entities.project.specs;

import it.smartcommunitylabdhub.core.models.base.specs.BaseSpec;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class ProjectBaseSpec<S extends ProjectBaseSpec<S>> extends BaseSpec<S> {

    String context;

    List<Object> functions = new ArrayList<>();

    List<Object> artifacts = new ArrayList<>();

    List<Object> workflows = new ArrayList<>();

    List<Object> dataitems = new ArrayList<>();


    @Override
    protected void configureSpec(S concreteSpec) {
        this.setContext(concreteSpec.getContext());
        this.setFunctions(concreteSpec.getFunctions());
        this.setArtifacts(concreteSpec.getArtifacts());
        this.setDataitems(concreteSpec.getDataitems());
        this.setWorkflows(concreteSpec.getWorkflows());
        this.setExtraSpecs(concreteSpec.getExtraSpecs());
    }
}
