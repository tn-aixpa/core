package it.smartcommunitylabdhub.core.controllers.v1.context;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.function.Function;
import it.smartcommunitylabdhub.commons.models.entities.task.Task;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.commons.services.entities.TaskService;
import it.smartcommunitylabdhub.core.ApplicationKeys;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.models.entities.function.FunctionEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.FunctionEntityFilter;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableFunctionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@ApiVersion("v1")
@RequestMapping("/-/{project}/functions")
@PreAuthorize(
    "hasAuthority('ROLE_ADMIN') or (hasAuthority(#project+':ROLE_USER') or hasAuthority(#project+':ROLE_ADMIN'))"
)
@Validated
@Slf4j
@Tag(name = "Function context API", description = "Endpoints related to functions management in project")
public class FunctionContextController {

    @Autowired
    SearchableFunctionService functionService;

    @Autowired
    TaskService taskService;

    @Operation(
        summary = "Create a function in a project context",
        description = "create the function for the project (context)"
    )
    @PostMapping(
        value = "",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Function createFunction(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @Valid @NotNull @RequestBody Function dto
    ) throws DuplicatedEntityException {
        //enforce project match
        dto.setProject(project);

        String name = dto.getName();

        try {
            @SuppressWarnings("unused")
            Function function = functionService.getLatestFunction(project, name);

            //there is already an entity with the same name
            throw new DuplicatedEntityException(name);
        } catch (NoSuchEntityException e) {
            //create as new
            return functionService.createFunction(dto);
        }
    }

    @Operation(
        summary = "Retrieve only the latest version of all functions",
        description = "return a list of the latest version of each function related to a project"
    )
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public Page<Function> getLatestFunctions(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @RequestParam(required = false) @Valid @Nullable FunctionEntityFilter filter,
        @PageableDefault(page = 0, size = ApplicationKeys.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults(
            { @SortDefault(sort = "created", direction = Direction.DESC) }
        ) Pageable pageable
    ) {
        SearchFilter<FunctionEntity> sf = null;
        if (filter != null) {
            sf = filter.toSearchFilter();
        }

        return functionService.searchLatestFunctionsByProject(project, pageable, sf);
    }

    @Operation(
        summary = "Retrieve all versions of the function sort by creation",
        description = "return a list of all version of the function sort by creation"
    )
    @GetMapping(path = "/{name}", produces = "application/json; charset=UTF-8")
    public Page<Function> getAllFunctionVersion(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PageableDefault(page = 0, size = ApplicationKeys.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults(
            { @SortDefault(sort = "created", direction = Direction.DESC) }
        ) Pageable pageable
    ) {
        return functionService.findFunctions(project, name, pageable);
    }

    @Operation(
        summary = "Create a new version of a function in a project context",
        description = "if function exist create a new version of the function"
    )
    @PostMapping(
        value = "/{name}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Function createOrUpdateFunction(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @RequestBody @Valid @NotNull Function dto
    ) throws NoSuchEntityException, DuplicatedEntityException {
        //enforce project match
        dto.setProject(project);
        dto.setName(name);

        @SuppressWarnings("unused")
        Function function = functionService.getLatestFunction(project, name);
        function = functionService.createFunction(dto);

        return function;
    }

    @Operation(
        summary = "Delete all version of a function",
        description = "First check if project exist, then delete a specific function version"
    )
    @DeleteMapping(path = "/{name}")
    public void deleteFunction(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name
    ) {
        functionService.deleteFunctions(project, name);
    }

    /*
     * Versions
     */

    @Operation(
        summary = "Retrieve the latest version of a function",
        description = "return the latest version of a function"
    )
    @GetMapping(path = "/{name}/latest", produces = "application/json; charset=UTF-8")
    public Function getLatestFunctionByName(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name
    ) throws NoSuchEntityException {
        return functionService.getLatestFunction(project, name);
    }

    @Operation(
        summary = "Retrieve a specific function version given the function id",
        description = "return a specific version of the function identified by the id"
    )
    @GetMapping(path = "/{name}/{id}", produces = "application/json; charset=UTF-8")
    public Function getFunctionById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        Function function = functionService.getFunction(id);

        //check for project and name match
        if (!function.getProject().equals(project) || !function.getName().equals(name)) {
            throw new IllegalArgumentException("invalid project or name");
        }

        return function;
    }

    @Operation(
        summary = "Update if exist a function in a project context",
        description = "First check if project exist, if function exist update."
    )
    @PutMapping(
        value = "/{name}/{id}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public Function updateFunction(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestBody @Valid @NotNull Function functionDTO
    ) throws NoSuchEntityException {
        Function function = functionService.getFunction(id);

        //check for project and name match
        if (!function.getProject().equals(project) || !function.getName().equals(name)) {
            throw new IllegalArgumentException("invalid project or name");
        }

        return functionService.updateFunction(id, functionDTO);
    }

    @Operation(
        summary = "Delete a specific function version, with optional cascade",
        description = "First check if project exist, then delete a specific function version"
    )
    @DeleteMapping(path = "/{name}/{id}")
    public void deleteFunction(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestParam(required = false) Boolean cascade
    ) throws NoSuchEntityException {
        Function function = functionService.getFunction(id);

        //check for project and name match
        if (!function.getProject().equals(project) || !function.getName().equals(name)) {
            throw new IllegalArgumentException("invalid project or name");
        }

        functionService.deleteFunction(id, cascade);
    }

    /*
     * Tasks
     */

    @Operation(
        summary = "List tasks for a given function",
        description = "Return a list of tasks defined for a specific function"
    )
    @GetMapping(path = "/{name}/{id}/tasks", produces = "application/json; charset=UTF-8")
    public List<Task> getTasks(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        Function function = functionService.getFunction(id);

        //check for project and name match
        if (!function.getProject().equals(project) || !function.getName().equals(name)) {
            throw new IllegalArgumentException("invalid project or name");
        }

        return taskService.getTasksByFunctionId(id);
    }
}
