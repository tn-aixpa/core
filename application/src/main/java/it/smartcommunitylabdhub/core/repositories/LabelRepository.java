package it.smartcommunitylabdhub.core.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import it.smartcommunitylabdhub.core.models.entities.label.LabelEntity;

public interface LabelRepository extends JpaRepository<LabelEntity, String>, JpaSpecificationExecutor<LabelEntity> {
	Page<LabelEntity> findByProject(String project, Pageable pageable);
	
	Page<LabelEntity> findByProjectAndLabelStartsWithIgnoreCase(String project, String label, Pageable pageable);
	
	LabelEntity findByProjectAndLabelIgnoreCase(String project, String label);
	
}
