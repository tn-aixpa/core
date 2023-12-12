package it.smartcommunitylabdhub.core.models.filters.abstracts;

import it.smartcommunitylabdhub.core.models.filters.interfaces.SpecificationFilter;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public abstract class AbstractSpecificationService<T, F extends SpecificationFilter<T>> {

    protected Specification<T> createSpecification(Map<String, String> filters, F entityFilter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Add your custom filters based on the provided map
            predicate = entityFilter.toPredicate(root, query, criteriaBuilder);

            // Add more conditions for other filters if needed

            return predicate;
        };
    }
}
