package it.smartcommunitylabdhub.commons.services.entities;

import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.workflow.Workflow;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;

/*
 * Service for managing workflows
 */
public interface WorkflowService {
    /**
     * List all workflows
     * @param pageable
     * @return
     */
    Page<Workflow> listWorkflows(Pageable pageable);

    /**
     * List all versions of every workflow for a user
     * @param user
     * @return
     */
    List<Workflow> listWorkflowsByUser(@NotNull String user);

    /**
     * List all versions of every workflow for a project
     * @param project
     * @return
     */
    List<Workflow> listWorkflowsByProject(@NotNull String project);

    /**
     * List all versions of every workflow for a project
     * @param project
     * @param pageable
     * @return
     */
    Page<Workflow> listWorkflowsByProject(@NotNull String project, Pageable pageable);

    /**
     * List the latest version of every workflow for a project
     * @param project
     * @return
     */
    List<Workflow> listLatestWorkflowsByProject(@NotNull String project);

    /**
     * List the latest version of every workflow for a project
     * @param project
     * @param pageable
     * @return
     */
    Page<Workflow> listLatestWorkflowsByProject(@NotNull String project, Pageable pageable);

    /**
     * Find all versions of a given workflow
     * @param project
     * @param name
     * @return
     */
    List<Workflow> findWorkflows(@NotNull String project, @NotNull String name);

    /**
     * Find all versions of a given workflow
     * @param project
     * @param name
     * @param pageable
     * @return
     */
    Page<Workflow> findWorkflows(@NotNull String project, @NotNull String name, Pageable pageable);

    /**
     * Find a specific workflow (version) via unique ID. Returns null if not found
     * @param id
     * @return
     */
    @Nullable
    Workflow findWorkflow(@NotNull String id);

    /**
     * Get a specific workflow (version) via unique ID. Throws exception if not found
     * @param id
     * @return
     * @throws NoSuchEntityException
     */
    Workflow getWorkflow(@NotNull String id) throws NoSuchEntityException;

    /**
     * Get the latest version of a given workflow
     * @param project
     * @param name
     * @return
     * @throws NoSuchEntityException
     */
    Workflow getLatestWorkflow(@NotNull String project, @NotNull String name) throws NoSuchEntityException;

    /**
     * Create a new workflow and store it
     * @param workflowDTO
     * @return
     * @throws DuplicatedEntityException
     */
    Workflow createWorkflow(@NotNull Workflow workflowDTO) throws DuplicatedEntityException;

    /**
     * Update a specific workflow version
     * @param id
     * @param workflowDTO
     * @return
     * @throws NoSuchEntityException
     */
    Workflow updateWorkflow(@NotNull String id, @NotNull Workflow workflowDTO) throws NoSuchEntityException;

    /**
     * Delete a specific workflow (version) via unique ID
     * @param id
     * @param cascade
     */
    void deleteWorkflow(@NotNull String id, @Nullable Boolean cascade);

    /**
     * Delete all versions of a given workflow
     * @param project
     * @param name
     */
    void deleteWorkflows(@NotNull String project, @NotNull String name);

    /**
     * Delete all workflows for a given project, with cascade.
     * @param project
     */
    void deleteWorkflowsByProject(@NotNull String project);
}
