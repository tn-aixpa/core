package it.smartcommunitylabdhub.core.controllers.v1.base;

import javax.annotation.Nullable;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.models.function.Function;
import it.smartcommunitylabdhub.core.ApplicationKeys;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;
import it.smartcommunitylabdhub.core.models.queries.filters.entities.FunctionEntityFilter;
import it.smartcommunitylabdhub.core.models.queries.services.SearchableTemplateService;
import jakarta.validation.Valid;

@RestController
@ApiVersion("v1")
@RequestMapping("/templates")
//TODO evaluate permissions for project via lookup in dto
@PreAuthorize("hasAuthority('ROLE_USER')")
@Validated
@Tag(name = "Template base API", description = "Endpoints related to entity templates management")
public class TemplateController {
	
	@Autowired
	SearchableTemplateService templateService;
	
    @Operation(summary = "List function's templates", description = "Return a list of all function's templates")
    @GetMapping(path = "functions", produces = "application/json; charset=UTF-8")
    public Page<Function> getFunctions(
        @ParameterObject @Valid @Nullable FunctionEntityFilter filter,
        @ParameterObject @PageableDefault(page = 0, size = ApplicationKeys.DEFAULT_PAGE_SIZE) @SortDefault.SortDefaults(
            { @SortDefault(sort = "id", direction = Direction.ASC) }) Pageable pageable) {
    	if(filter == null)
    		filter = new FunctionEntityFilter();
        return templateService.searchFunctions(pageable, filter);
    }


}
