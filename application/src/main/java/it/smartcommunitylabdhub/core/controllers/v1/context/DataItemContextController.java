package it.smartcommunitylabdhub.core.controllers.v1.context;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.core.ApplicationKeys;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.models.entities.dataitem.DataItemEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.DataItemEntityFilter;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableDataItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ApiVersion("v1")
@RequestMapping("/-/{project}/dataitems")
@PreAuthorize(
    "hasAuthority('ROLE_ADMIN') or (hasAuthority(#project+':ROLE_USER') or hasAuthority(#project+':ROLE_ADMIN'))"
)
@Validated
@Slf4j
@Tag(name = "DataItem context API", description = "Endpoints related to dataitems management in project")
public class DataItemContextController {

    @Autowired
    SearchableDataItemService dataItemService;

    @Operation(
        summary = "Create a dataItem in a project context",
        description = "create the dataItem for the project (context)"
    )
    @PostMapping(
        value = "",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public DataItem createDataItem(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @Valid @NotNull @RequestBody DataItem dto
    ) throws DuplicatedEntityException {
        //enforce project match
        dto.setProject(project);

        //create as new
        return dataItemService.createDataItem(dto);
    }

    @Operation(
        summary = "Retrieve only the latest version of all dataItems",
        description = "return a list of the latest version of each dataItem related to a project"
    )
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public Page<DataItem> getLatestDataItems(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @ParameterObject @RequestParam(required = false) @Valid @Nullable DataItemEntityFilter filter,
        @ParameterObject @PageableDefault(page = 0, size = ApplicationKeys.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults(
            { @SortDefault(sort = "created", direction = Direction.DESC) }
        ) Pageable pageable
    ) {
        SearchFilter<DataItemEntity> sf = null;
        if (filter != null) {
            sf = filter.toSearchFilter();
        }

        return dataItemService.searchLatestDataItemsByProject(project, pageable, sf);
    }

    @Operation(
        summary = "Retrieve all versions of the dataItem sort by creation",
        description = "return a list of all version of the dataItem sort by creation"
    )
    @GetMapping(path = "/{name}", produces = "application/json; charset=UTF-8")
    public Page<DataItem> getAllDataItemVersion(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @ParameterObject @PageableDefault(page = 0, size = ApplicationKeys.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults(
            { @SortDefault(sort = "created", direction = Direction.DESC) }
        ) Pageable pageable
    ) {
        return dataItemService.findDataItems(project, name, pageable);
    }

    @Operation(
        summary = "Create a new version of a dataItem in a project context",
        description = "if dataItem exist create a new version of the dataItem"
    )
    @PostMapping(
        value = "/{name}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public DataItem createOrUpdateDataItem(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @RequestBody @Valid @NotNull DataItem dto
    ) throws NoSuchEntityException, DuplicatedEntityException {
        //enforce project match
        dto.setProject(project);
        dto.setName(name);

        @SuppressWarnings("unused")
        DataItem dataItem = dataItemService.getLatestDataItem(project, name);
        dataItem = dataItemService.createDataItem(dto);

        return dataItem;
    }

    @Operation(
        summary = "Delete all version of a dataItem",
        description = "First check if project exist, then delete a specific dataItem version"
    )
    @DeleteMapping(path = "/{name}")
    public void deleteDataItem(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name
    ) {
        dataItemService.deleteDataItems(project, name);
    }

    /*
     * Versions
     */

    @Operation(
        summary = "Retrieve the latest version of a dataItem",
        description = "return the latest version of a dataItem"
    )
    @GetMapping(path = "/{name}/latest", produces = "application/json; charset=UTF-8")
    public DataItem getLatestDataItemByName(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name
    ) throws NoSuchEntityException {
        return dataItemService.getLatestDataItem(project, name);
    }

    @Operation(
        summary = "Retrieve a specific dataItem version given the dataItem id",
        description = "return a specific version of the dataItem identified by the id"
    )
    @GetMapping(path = "/{name}/{id}", produces = "application/json; charset=UTF-8")
    public DataItem getDataItemById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        DataItem dataItem = dataItemService.getDataItem(id);

        //check for project and name match
        if (!dataItem.getProject().equals(project) || !dataItem.getName().equals(name)) {
            throw new IllegalArgumentException("invalid project or name");
        }

        return dataItem;
    }

    @Operation(
        summary = "Update if exist a dataItem in a project context",
        description = "First check if project exist, if dataItem exist update."
    )
    @PutMapping(
        value = "/{name}/{id}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public DataItem updateDataItem(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestBody @Valid @NotNull DataItem dataItemDTO
    ) throws NoSuchEntityException {
        DataItem dataItem = dataItemService.getDataItem(id);

        //check for project and name match
        if (!dataItem.getProject().equals(project) || !dataItem.getName().equals(name)) {
            throw new IllegalArgumentException("invalid project or name");
        }

        return dataItemService.updateDataItem(id, dataItemDTO);
    }

    @Operation(
        summary = "Delete a specific dataItem version",
        description = "First check if project exist, then delete a specific dataItem version"
    )
    @DeleteMapping(path = "/{name}/{id}")
    public void deleteDataItem(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        DataItem dataItem = dataItemService.getDataItem(id);

        //check for project and name match
        if (!dataItem.getProject().equals(project) || !dataItem.getName().equals(name)) {
            throw new IllegalArgumentException("invalid project or name");
        }

        dataItemService.deleteDataItem(id);
    }
}
