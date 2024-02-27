package it.smartcommunitylabdhub.commons.services;

import it.smartcommunitylabdhub.commons.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.project.Project;
import it.smartcommunitylabdhub.commons.models.entities.secret.Secret;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/*
 * Service for managing artifacts
 */
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
