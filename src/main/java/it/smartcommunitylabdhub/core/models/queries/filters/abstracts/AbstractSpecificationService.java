package it.smartcommunitylabdhub.core.models.queries.filters.abstracts;

import it.smartcommunitylabdhub.core.models.queries.filters.interfaces.SpecificationFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public abstract class AbstractSpecificationService<T, F extends SpecificationFilter<T>> {

    protected Specification<T> createSpecification(Map<String, String> filter, F entityFilter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Add your custom filter based on the provided map
            predicate = entityFilter.toPredicate(root, query, criteriaBuilder);

            // Add more conditions for other filter if needed

            return predicate;
        };
    }
}
