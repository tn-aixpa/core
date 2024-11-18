package it.smartcommunitylabdhub.core.models.specs.project;

import it.smartcommunitylabdhub.commons.annotations.common.SpecType;
import it.smartcommunitylabdhub.commons.models.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.entities.EntityName;
import it.smartcommunitylabdhub.commons.models.function.Function;
import it.smartcommunitylabdhub.commons.models.model.Model;
import it.smartcommunitylabdhub.commons.models.project.ProjectBaseSpec;
import it.smartcommunitylabdhub.commons.models.workflow.Workflow;
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

    private List<Function> functions = new ArrayList<>();

    private List<Artifact> artifacts = new ArrayList<>();

    private List<Workflow> workflows = new ArrayList<>();

    private List<DataItem> dataitems = new ArrayList<>();

    private List<Model> models = new ArrayList<>();

    @Override
    public void configure(Map<String, Serializable> data) {
        ProjectSpec concreteSpec = mapper.convertValue(data, ProjectSpec.class);

        this.functions = concreteSpec.getFunctions();
        this.artifacts = concreteSpec.getArtifacts();
        this.dataitems = concreteSpec.getDataitems();
        this.models = concreteSpec.getModels();
        this.workflows = concreteSpec.getWorkflows();

        super.configure(data);
    }
}
