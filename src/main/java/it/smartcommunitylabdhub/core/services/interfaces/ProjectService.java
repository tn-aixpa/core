package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.core.models.entities.function.Function;
import it.smartcommunitylabdhub.core.models.entities.project.Project;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {

    Page<Project> getProjects(Pageable pageable);

    Project createProject(Project projectDTO);

    Project getProject(String uuidOrName);

    Project updateProject(Project projectDTO, String uuidOrName);

    boolean deleteProject(String uuidOrName);

    boolean deleteProjectByName(String name);

    List<Function> getProjectFunctions(String uuidOrName);

    List<Artifact> getProjectArtifacts(String uuidOrName);

    List<Workflow> getProjectWorkflows(String uuidOrName);

}
