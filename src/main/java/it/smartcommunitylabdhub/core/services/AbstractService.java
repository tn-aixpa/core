package it.smartcommunitylabdhub.core.services;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public class AbstractService<T> {

    private Specification<T> createSpecification(Map<String, String> filters) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

//            // Add your custom filters based on the provided map
//            if (filters.containsKey("functionName")) {
//                predicate = criteriaBuilder.and(predicate,
//                        new FunctionEntityFilter(filters.get("functionName"))
//                                .toPredicate(root, query, criteriaBuilder));
//            }

            // Add more conditions for other filters if needed

            return predicate;
        };
    }
}
