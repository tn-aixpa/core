package it.smartcommunitylabdhub.core.models.queries.filters.entities;

import it.smartcommunitylabdhub.core.models.entities.workflow.WorkflowEntity;
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
public class WorkflowEntityFilter extends BaseEntityFilter implements SpecificationFilter<WorkflowEntity> {


    @Override
    public Predicate toPredicate(Root<WorkflowEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.conjunction();
        if (getName() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("name"), "%" + getName() + "%"));
        }

        if (getKind() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("kind"), "%" + getKind() + "%"));
        }

        if (getProject() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("project"), "%" + getProject() + "%"));
        }

        if (getState() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("state"), "%" + getState() + "%"));
        }

        if (getCreatedDate() != null) {

            DateUtils.DateInterval dateInterval = DateUtils.parseDateIntervalFromTimestamps(getCreatedDate(), true);
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.between(root.get("created"), dateInterval.startDate(), dateInterval.endDate()));

        }

        return predicate;
    }
}
