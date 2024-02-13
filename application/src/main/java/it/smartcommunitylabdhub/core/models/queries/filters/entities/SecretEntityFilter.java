package it.smartcommunitylabdhub.core.models.queries.filters.entities;

import it.smartcommunitylabdhub.commons.utils.DateUtils;
import it.smartcommunitylabdhub.core.models.entities.secret.SecretEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.interfaces.SpecificationFilter;
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
public class SecretEntityFilter extends BaseEntityFilter implements SpecificationFilter<SecretEntity> {

    @Override
    public Predicate toPredicate(Root<SecretEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (getName() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(root.get("name"), "%" + getName() + "%"));
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

        return predicate;
    }
}
