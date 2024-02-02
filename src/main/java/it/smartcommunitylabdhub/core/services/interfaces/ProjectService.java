package it.smartcommunitylabdhub.core.services.interfaces;

import it.smartcommunitylabdhub.core.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.core.models.entities.function.Function;
import it.smartcommunitylabdhub.core.models.entities.project.Project;
import it.smartcommunitylabdhub.core.models.entities.secret.Secret;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProjectService {

    Page<Project> getProjects(Map<String, String> filter, Pageable pageable);

    Project createProject(Project projectDTO);

    Project getProject(String name);

    Project updateProject(Project projectDTO, String name);

    boolean deleteProject(String name, Boolean cascade);

    boolean deleteProjectByName(String name);

    List<Function> getProjectFunctions(String name);

    List<Artifact> getProjectArtifacts(String name);

    List<Workflow> getProjectWorkflows(String name);

    List<Secret> getProjectSecrets(String name);
    Map<String, String> getProjectSecretData(String name, Set<String> keys);
    void storeProjectSecretData(String name, Map<String, String> values);


}
