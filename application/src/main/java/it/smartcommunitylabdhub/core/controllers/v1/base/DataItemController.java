package it.smartcommunitylabdhub.core.controllers.v1.base;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.annotations.validators.ValidateField;
import it.smartcommunitylabdhub.commons.exceptions.DuplicatedEntityException;
import it.smartcommunitylabdhub.commons.exceptions.NoSuchEntityException;
import it.smartcommunitylabdhub.commons.models.entities.dataitem.DataItem;
import it.smartcommunitylabdhub.commons.services.DataItemService;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/dataitems")
@ApiVersion("v1")
@Validated
@Tag(name = "DataItem base API", description = "Endpoints related to dataitems management out of the Context")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class DataItemController {

    @Autowired
    DataItemService dataItemService;

    @Operation(summary = "List dataItems", description = "Return a list of all dataItems")
    @GetMapping(path = "", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Page<DataItem>> getDataItems(@RequestParam Map<String, String> filter, Pageable pageable) {
        return ResponseEntity.ok(this.dataItemService.getDataItems(filter, pageable));
    }

    @Operation(summary = "Create dataItem", description = "Create an dataItem and return")
    @PostMapping(
        value = "",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<DataItem> createDataItem(@Valid @RequestBody DataItem dataItemDTO)
        throws DuplicatedEntityException {
        return ResponseEntity.ok(this.dataItemService.createDataItem(dataItemDTO));
    }

    @Operation(summary = "Get a dataItem by uuid", description = "Return an dataItem")
    @GetMapping(path = "/{uuid}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<DataItem> getDataItem(
        @ValidateField @PathVariable(name = "uuid", required = true) String uuid
    ) throws NoSuchEntityException {
        return ResponseEntity.ok(this.dataItemService.getDataItem(uuid));
    }

    @Operation(summary = "Update specific dataItem", description = "Update and return the dataItem")
    @PutMapping(
        path = "/{uuid}",
        consumes = { MediaType.APPLICATION_JSON_VALUE, "application/x-yaml" },
        produces = "application/json; charset=UTF-8"
    )
    public ResponseEntity<DataItem> updateDataItem(
        @Valid @RequestBody DataItem dataItemDTO,
        @ValidateField @PathVariable String uuid
    ) throws NoSuchEntityException {
        return ResponseEntity.ok(this.dataItemService.updateDataItem(uuid, dataItemDTO));
    }

    @Operation(summary = "Delete a dataItem", description = "Delete a specific dataItem")
    @DeleteMapping(path = "/{uuid}")
    public ResponseEntity<Boolean> deleteDataItem(@ValidateField @PathVariable String uuid) {
        this.dataItemService.deleteDataItem(uuid);
        return ResponseEntity.ok().build();
    }
}
