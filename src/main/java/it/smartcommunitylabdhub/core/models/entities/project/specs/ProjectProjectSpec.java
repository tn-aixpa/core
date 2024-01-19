package it.smartcommunitylabdhub.core.models.entities.project.specs;

import it.smartcommunitylabdhub.core.annotations.common.SpecType;
import it.smartcommunitylabdhub.core.components.infrastructure.enums.EntityName;

@SpecType(kind = "project", entity = EntityName.PROJECT, factory = ProjectProjectSpec.class)
public class ProjectProjectSpec extends ProjectBaseSpec<ProjectProjectSpec> {
    @Override
    protected void configureSpec(ProjectProjectSpec projectProjectSpec) {
        super.configureSpec(projectProjectSpec);
    }
}
