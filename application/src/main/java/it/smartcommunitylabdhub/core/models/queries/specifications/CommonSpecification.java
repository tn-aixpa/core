package it.smartcommunitylabdhub.core.models.queries.specifications;

import it.smartcommunitylabdhub.commons.models.base.BaseEntity;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class CommonSpecification {

    public static <T extends BaseEntity> Specification<T> latestByProject(String project) {
        return (root, query, criteriaBuilder) -> {
            Subquery<Number> subquery = query.subquery(Number.class);
            Root<T> subqueryRoot = (Root<T>) subquery.from(root.getJavaType());

            subquery.select(criteriaBuilder.max(subqueryRoot.get("created")));
            subquery.where(
                criteriaBuilder.equal(subqueryRoot.get("project"), project),
                criteriaBuilder.equal(subqueryRoot.get("name"), root.get("name"))
            );
            subquery.groupBy(subqueryRoot.get("name"), subqueryRoot.get("project"));

            return criteriaBuilder.and(
                criteriaBuilder.equal(root.get("project"), project),
                criteriaBuilder.in(root.get("created")).value(subquery)
            );
        };
    }
}
