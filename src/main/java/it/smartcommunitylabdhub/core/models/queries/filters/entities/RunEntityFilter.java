package it.smartcommunitylabdhub.core.models.queries.filters.entities;

import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.interfaces.SpecificationFilter;
import it.smartcommunitylabdhub.core.utils.DateUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;


@Component
@Getter
@Setter
public class RunEntityFilter extends BaseEntityFilter implements SpecificationFilter<RunEntity> {

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
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get("created"), dateInterval.startDate(), dateInterval.endDate()));

        }


        // add more..here...

        return predicate;
    }
}
