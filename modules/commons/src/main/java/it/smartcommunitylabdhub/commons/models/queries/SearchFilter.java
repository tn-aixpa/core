package it.smartcommunitylabdhub.commons.models.queries;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public interface SearchFilter<T> {
    List<SearchCriteria<T>> getCriteria();

    Condition getCondition();

    Specification<T> toSpecification();

    enum Condition {
        and,
        or
    }
}
