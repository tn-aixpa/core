package it.smartcommunitylabdhub.core.services;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public class AbstractService<T> {

    private Specification<T> createSpecification(Map<String, String> filter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

//            // Add your custom filter based on the provided map
//            if (filter.containsKey("functionName")) {
//                predicate = criteriaBuilder.and(predicate,
//                        new FunctionEntityFilter(filter.get("functionName"))
//                                .toPredicate(root, query, criteriaBuilder));
//            }

            // Add more conditions for other filter if needed

            return predicate;
        };
    }
}
