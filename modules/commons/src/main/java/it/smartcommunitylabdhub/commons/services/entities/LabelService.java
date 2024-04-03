package it.smartcommunitylabdhub.commons.services.entities;

import it.smartcommunitylabdhub.commons.models.entities.label.Label;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {
	public Page<Label> findByProject(String project, Pageable pageable);
	public Page<Label> findByProjectAndLabelStartsWithIgnoreCase(String project, String label, Pageable pageable);
}
