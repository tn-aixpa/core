package it.smartcommunitylabdhub.commons.models.queries;

import it.smartcommunitylabdhub.commons.models.base.BaseEntity;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public interface SearchFilter<T extends BaseEntity> {
    List<SearchCriteria<T>> getCriteria();

    Condition getCondition();

    Specification<T> toSpecification();

    enum Condition {
        and,
        or,
    }
}
