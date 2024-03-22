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

    @Operation(summary = "Create a dataItem in a project context")
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

    @Operation(summary = "Search dataItems")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public Page<DataItem> searchDataItems(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @ParameterObject @Valid @Nullable DataItemEntityFilter filter,
        @ParameterObject @RequestParam(required = false, defaultValue = "latest") String versions,
        @ParameterObject @PageableDefault(page = 0, size = ApplicationKeys.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults(
            { @SortDefault(sort = "created", direction = Direction.DESC) }
        ) Pageable pageable
    ) {
        SearchFilter<DataItemEntity> sf = null;
        if (filter != null) {
            sf = filter.toSearchFilter();
        }

        if ("all".equals(versions)) {
            return dataItemService.searchDataItemsByProject(project, pageable, sf);
        } else {
            return dataItemService.searchLatestDataItemsByProject(project, pageable, sf);
        }
    }

    @Operation(summary = "Delete all version of a dataItem")
    @DeleteMapping(path = "")
    public void deleteAllDataItem(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @ParameterObject @RequestParam @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String name
    ) {
        dataItemService.deleteDataItems(project, name);
    }

    /*
     * Versions
     */

    @Operation(summary = "Retrieve a specific dataItem version given the dataItem id")
    @GetMapping(path = "/{id}", produces = "application/json; charset=UTF-8")
    public DataItem getDataItemById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        DataItem dataItem = dataItemService.getDataItem(id);

        //check for project and name match
        if (!dataItem.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        return dataItem;
    }

    @Operation(summary = "Update if exist a dataItem in a project context")
    @PutMapping(
        value = "/{id}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public DataItem updateDataItemById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id,
        @RequestBody @Valid @NotNull DataItem dataItemDTO
    ) throws NoSuchEntityException {
        DataItem dataItem = dataItemService.getDataItem(id);

        //check for project and name match
        if (!dataItem.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        return dataItemService.updateDataItem(id, dataItemDTO);
    }

    @Operation(
        summary = "Delete a specific dataItem version",
        description = "First check if project exist, then delete a specific dataItem version"
    )
    @DeleteMapping(path = "/{id}")
    public void deleteDataItemById(
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String project,
        @PathVariable @Valid @NotNull @Pattern(regexp = Keys.SLUG_PATTERN) String id
    ) throws NoSuchEntityException {
        DataItem dataItem = dataItemService.getDataItem(id);

        //check for project and name match
        if (!dataItem.getProject().equals(project)) {
            throw new IllegalArgumentException("invalid project");
        }

        dataItemService.deleteDataItem(id);
    }
}
