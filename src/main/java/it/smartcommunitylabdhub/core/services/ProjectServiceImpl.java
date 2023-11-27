package it.smartcommunitylabdhub.core.services;

import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.exceptions.CustomException;
import it.smartcommunitylabdhub.core.models.builders.artifact.ArtifactDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.function.FunctionDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.project.ProjectDTOBuilder;
import it.smartcommunitylabdhub.core.models.builders.project.ProjectEntityBuilder;
import it.smartcommunitylabdhub.core.models.builders.workflow.WorkflowDTOBuilder;
import it.smartcommunitylabdhub.core.models.entities.artifact.Artifact;
import it.smartcommunitylabdhub.core.models.entities.artifact.ArtifactEntity;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.core.models.entities.function.Function;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.entities.project.Project;
import it.smartcommunitylabdhub.core.models.entities.project.ProjectEntity;
import it.smartcommunitylabdhub.core.models.entities.workflow.Workflow;
import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowDTO;
import it.smartcommunitylabdhub.core.repositories.*;
import it.smartcommunitylabdhub.core.services.interfaces.ProjectService;
import it.smartcommunitylabdhub.core.utils.ErrorList;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    FunctionRepository functionRepository;

    @Autowired
    ArtifactRepository artifactRepository;

    @Autowired
    WorkflowRepository workflowRepository;

    @Autowired
    DataItemRepository dataItemRepository;

    @Autowired
    LogRepository logRepository;

    @Autowired
    RunRepository runRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    ProjectDTOBuilder projectDTOBuilder;

    @Autowired
    ProjectEntityBuilder projectEntityBuilder;

    @Autowired
    ArtifactDTOBuilder artifactDTOBuilder;

    @Autowired
    FunctionDTOBuilder functionDTOBuilder;

    @Autowired
    WorkflowDTOBuilder workflowDTOBuilder;

    @Override
    public Project getProject(String uuidOrName) {

        return projectRepository.findById(uuidOrName)
                .or(() -> projectRepository.findByName(uuidOrName))
                .map(project -> {
                    List<FunctionEntity> functions = functionRepository.findByProject(project.getName());
                    List<ArtifactEntity> artifacts = artifactRepository.findByProject(project.getName());
                    List<Workflow> workflows = workflowRepository.findByProject(project.getName());
                    List<DataItem> dataItems = dataItemRepository.findByProject(project.getName());

                    return projectDTOBuilder.build(project, artifacts, functions, workflows,
                            dataItems, true);
                })
                .orElseThrow(() -> new CoreException(
                        ErrorList.PROJECT_NOT_FOUND.getValue(),
                        ErrorList.PROJECT_NOT_FOUND.getReason(),
                        HttpStatus.NOT_FOUND));
    }

    @Override
    public Page<Project> getProjects(Pageable pageable) {
        try {
            Page<ProjectEntity> projectPage = this.projectRepository.findAll(pageable);
            return new PageImpl<>(
                    projectPage.getContent().stream().map((project) -> {
                        List<FunctionEntity> functions = functionRepository.findByProject(project.getName());
                        List<ArtifactEntity> artifacts = artifactRepository.findByProject(project.getName());
                        List<Workflow> workflows = workflowRepository.findByProject(project.getName());
                        List<DataItem> dataItems = dataItemRepository.findByProject(project.getName());

                        return projectDTOBuilder.build(project, artifacts, functions, workflows,
                                dataItems, true);
                    }).collect(Collectors.toList()), pageable, projectPage.getContent().size());
        } catch (CustomException e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public Project createProject(Project projectDTO) {
        if ((projectDTO.getId() != null && projectRepository.existsById(projectDTO.getId())) ||
                projectRepository.existsByName(projectDTO.getName())) {
            throw new CoreException(ErrorList.DUPLICATE_PROJECT.getValue(),
                    ErrorList.DUPLICATE_PROJECT.getReason(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return Optional.of(projectEntityBuilder.build(projectDTO))
                .map(project -> {
                    projectRepository.save(project);
                    return projectDTOBuilder.build(project, List.of(), List.of(), List.of(),
                            List.of(), true);
                })
                .orElseThrow(() -> new CoreException(
                        ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                        "Failed to generate the project.",
                        HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Override
    public Project updateProject(Project projectDTO, String uuidOrName) {

        return Optional.ofNullable(projectDTO.getId())
                .filter(id -> id.equals(uuidOrName))
                .or(() -> Optional.ofNullable(projectDTO.getName())
                        .filter(name -> name.equals(uuidOrName)))
                .map(id -> projectRepository.findById(uuidOrName)
                        .or(() -> projectRepository.findByName(uuidOrName))
                        .orElseThrow(() -> new CoreException(
                                ErrorList.PROJECT_NOT_FOUND.getValue(),
                                ErrorList.PROJECT_NOT_FOUND.getReason(),
                                HttpStatus.NOT_FOUND)))
                .map(project -> {
                    final ProjectEntity projectUpdated = projectEntityBuilder.update(project, projectDTO);
                    this.projectRepository.save(projectUpdated);

                    List<FunctionEntity> functions =
                            functionRepository.findByProject(projectUpdated.getName());
                    List<ArtifactEntity> artifacts =
                            artifactRepository.findByProject(projectUpdated.getName());
                    List<Workflow> workflows =
                            workflowRepository.findByProject(projectUpdated.getName());
                    List<DataItem> dataItems =
                            dataItemRepository.findByProject(projectUpdated.getName());

                    return projectDTOBuilder.build(projectUpdated, artifacts, functions, workflows,
                            dataItems,
                            true);
                })
                .orElseThrow(() -> new CoreException(
                        ErrorList.PROJECT_NOT_MATCH.getValue(),
                        ErrorList.PROJECT_NOT_MATCH.getReason(),
                        HttpStatus.NOT_FOUND));

    }

    @Override
    @Transactional
    public boolean deleteProject(String uuidOrName) {
        return Optional.ofNullable(uuidOrName)
                .map(value -> {
                    boolean deleted = false;
                    if (projectRepository.existsById(value)) {
                        projectRepository.findById(value).ifPresent(project -> {
                            // delete functions, artifacts, workflow, dataitems
                            this.artifactRepository.deleteByProjectName(project.getName());
                            this.dataItemRepository.deleteByProjectName(project.getName());
                            this.workflowRepository.deleteByProjectName(project.getName());
                            this.functionRepository.deleteByProjectName(project.getName());
                            this.dataItemRepository.deleteByProjectName(project.getName());
                            this.logRepository.deleteByProjectName(project.getName());
                            this.runRepository.deleteByProjectName(project.getName());
                            this.taskRepository.deleteByProjectName(project.getName());
                        });
                        projectRepository.deleteById(value);
                        deleted = true;
                    } else if (projectRepository.existsByName(value)) {
                        projectRepository.findByName(value).ifPresent(project -> {
                            // delete functions, artifacts, workflow, dataitems
                            this.artifactRepository.deleteByProjectName(project.getName());
                            this.dataItemRepository.deleteByProjectName(project.getName());
                            this.workflowRepository.deleteByProjectName(project.getName());
                            this.functionRepository.deleteByProjectName(project.getName());
                            this.dataItemRepository.deleteByProjectName(project.getName());
                            this.logRepository.deleteByProjectName(project.getName());
                            this.runRepository.deleteByProjectName(project.getName());
                            this.taskRepository.deleteByProjectName(project.getName());
                        });
                        projectRepository.deleteByName(value);
                        deleted = true;
                    }
                    if (!deleted) {
                        throw new CoreException(
                                ErrorList.PROJECT_NOT_FOUND.getValue(),
                                ErrorList.PROJECT_NOT_FOUND.getReason(),
                                HttpStatus.NOT_FOUND);
                    }
                    return deleted;
                })
                .orElseThrow(() -> new CoreException(
                        ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                        "Cannot delete project",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public List<Function> getProjectFunctions(String uuidOrName) {

        return Optional.of(projectRepository.findById(uuidOrName)
                        .or(() -> projectRepository.findByName(uuidOrName)))
                .orElseThrow(() -> new CoreException(
                        ErrorList.PROJECT_NOT_FOUND.getValue(),
                        ErrorList.PROJECT_NOT_FOUND.getReason(),
                        HttpStatus.NOT_FOUND))
                .map(ProjectEntity::getName)
                .flatMap(projectName -> {
                    try {
                        List<FunctionEntity> functions = functionRepository.findByProject(projectName);
                        return Optional.of(
                                functions.stream()
                                        .map(function -> functionDTOBuilder.build(function, false))
                                        .collect(Collectors.toList()));
                    } catch (CustomException e) {
                        throw new CoreException(
                                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                                e.getMessage(),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                })
                .orElseThrow(() -> new CoreException(
                        ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                        "Error occurred while retrieving functions.",
                        HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Override
    public List<Artifact> getProjectArtifacts(String uuidOrName) {
        return Optional.of(projectRepository.findById(uuidOrName)
                        .or(() -> projectRepository.findByName(uuidOrName)))
                .orElseThrow(() -> new CoreException(
                        ErrorList.PROJECT_NOT_FOUND.getValue(),
                        ErrorList.PROJECT_NOT_FOUND.getReason(),
                        HttpStatus.NOT_FOUND))
                .map(ProjectEntity::getName)
                .flatMap(projectName -> {
                    try {
                        List<ArtifactEntity> artifacts = artifactRepository.findByProject(projectName);
                        return Optional.of(
                                artifacts.stream().map(
                                                artifact -> artifactDTOBuilder.build(artifact, false))
                                        .collect(Collectors.toList()));
                    } catch (CustomException e) {
                        throw new CoreException(
                                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                                e.getMessage(),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                })
                .orElseThrow(() -> new CoreException(
                        ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                        "Error occurred while retrieving artifacts.",
                        HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Override
    public List<WorkflowDTO> getProjectWorkflows(String uuidOrName) {
        return Optional.of(projectRepository.findById(uuidOrName)
                        .or(() -> projectRepository.findByName(uuidOrName)))
                .orElseThrow(() -> new CoreException(
                        ErrorList.PROJECT_NOT_FOUND.getValue(),
                        ErrorList.PROJECT_NOT_FOUND.getReason(),
                        HttpStatus.NOT_FOUND))
                .map(ProjectEntity::getName)
                .flatMap(projectName -> {
                    try {
                        List<Workflow> workflows = workflowRepository.findByProject(projectName);
                        return Optional.of(
                                workflows.stream()
                                        .map(workflow -> workflowDTOBuilder.build(workflow, false))
                                        .collect(Collectors.toList()));
                    } catch (CustomException e) {
                        throw new CoreException(
                                ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                                e.getMessage(),
                                HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                })
                .orElseThrow(() -> new CoreException(
                        ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                        "Error occurred while retrieving workflows.",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Override
    public boolean deleteProjectByName(String name) {
        try {
            if (this.projectRepository.existsByName(name)) {
                this.projectRepository.deleteByName(name);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new CoreException(
                    ErrorList.INTERNAL_SERVER_ERROR.getValue(),
                    "cannot delete project",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
