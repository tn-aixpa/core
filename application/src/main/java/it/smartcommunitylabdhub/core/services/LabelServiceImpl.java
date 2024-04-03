package it.smartcommunitylabdhub.core.services;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import it.smartcommunitylabdhub.commons.models.entities.label.Label;
import it.smartcommunitylabdhub.commons.services.entities.LabelService;
import it.smartcommunitylabdhub.core.exceptions.CoreException;
import it.smartcommunitylabdhub.core.models.builders.label.LabelDTOBuilder;
import it.smartcommunitylabdhub.core.models.entities.label.LabelEntity;
import it.smartcommunitylabdhub.core.repositories.LabelRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class LabelServiceImpl implements LabelService {
	@Autowired
	LabelRepository labelRepository;
	
	@Autowired
	LabelDTOBuilder labelDTOBuilder;
	
	public Page<Label> findByProject(String project, Pageable pageable) {
		try {
			Page<LabelEntity> page = labelRepository.findByProject(project, pageable);
            return new PageImpl<>(
                    page.getContent().stream().map(e -> labelDTOBuilder.build(e)).collect(Collectors.toList()),
                    pageable,
                    page.getTotalElements()
                );			
		} catch (Exception e) {
			throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public Page<Label> findByProjectAndLabelStartsWithIgnoreCase(String project, String label, Pageable pageable) {
		try {
			Page<LabelEntity> page = labelRepository.findByProjectAndLabelStartsWithIgnoreCase(project, label, pageable);
            return new PageImpl<>(
                    page.getContent().stream().map(e -> labelDTOBuilder.build(e)).collect(Collectors.toList()),
                    pageable,
                    page.getTotalElements()
                );			
		} catch (Exception e) {
			throw new CoreException("InternalServerError", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
