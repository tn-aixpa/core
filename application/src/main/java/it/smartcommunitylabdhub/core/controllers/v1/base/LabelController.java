package it.smartcommunitylabdhub.core.controllers.v1.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.smartcommunitylabdhub.commons.models.entities.label.Label;
import it.smartcommunitylabdhub.commons.services.entities.LabelService;
import it.smartcommunitylabdhub.core.annotations.ApiVersion;

@RestController
@RequestMapping("/labels")
@ApiVersion("v1")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Label base API", description = "Endpoints related to labels search")
public class LabelController {
	@Autowired
	LabelService labelService;
	
	@Operation(summary = "Search labels", description = "Return a list of labels within a project and starting with a specific text")
	@GetMapping(path = "", produces = "application/json; charset=UTF-8")
	public ResponseEntity<Page<Label>> getLabels(
			@RequestParam String project, 
			@RequestParam(required = false) String label, Pageable pageable) {
		if(StringUtils.hasText(label)) {
			return ResponseEntity.ok(labelService.findByProjectAndLabelStartsWithIgnoreCase(project, label.trim(), pageable));
		} else {
			return ResponseEntity.ok(labelService.findByProject(project, pageable));
		}
	}

}
