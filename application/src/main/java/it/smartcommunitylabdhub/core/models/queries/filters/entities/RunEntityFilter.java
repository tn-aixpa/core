package it.smartcommunitylabdhub.core.models.queries.filters.entities;

import it.smartcommunitylabdhub.commons.models.base.BaseEntitySearchCriteria;
import it.smartcommunitylabdhub.commons.utils.DateUtils;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.interfaces.SpecificationFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RunEntityFilter implements SpecificationFilter<RunEntity> {

    private String name;
    private String kind;
    private String project;
    private String state;
    private String createdDate;
    private String task;
    private String taskId;

    @Override
    public Predicate toPredicate(Root<RunEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (task != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("task"), "%" + task + "%"));
        }
        if (taskId != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("taskId"), "%" + taskId + "%"));
        }

        if (getKind() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("kind"), "%" + getKind() + "%"));
        }

        if (getState() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("state"), "%" + getState() + "%"));
        }

        if (getCreatedDate() != null) {
            DateUtils.DateInterval dateInterval = DateUtils.parseDateIntervalFromTimestamps(getCreatedDate(), true);
            predicate =
                criteriaBuilder.and(
                    predicate,
                    criteriaBuilder.between(root.get("created"), dateInterval.startDate(), dateInterval.endDate())
                );
        }

        // add more..here...

        return predicate;
    }
}
