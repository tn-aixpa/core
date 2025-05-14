package it.smartcommunitylabdhub.core.labels.persistence;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LabelRepository extends JpaRepository<LabelEntity, String>, JpaSpecificationExecutor<LabelEntity> {
    List<LabelEntity> findByProject(String project);

    Page<LabelEntity> findByProject(String project, Pageable pageable);

    Page<LabelEntity> findByProjectAndLabelStartsWithIgnoreCase(String project, String label, Pageable pageable);

    LabelEntity findByProjectAndLabelIgnoreCase(String project, String label);
}
