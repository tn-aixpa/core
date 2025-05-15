package it.smartcommunitylabdhub.core.queries.specifications;

import it.smartcommunitylabdhub.commons.Fields;
import it.smartcommunitylabdhub.core.persistence.AbstractEntity_;
import it.smartcommunitylabdhub.core.persistence.BaseEntity;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

public class CommonSpecification {

    public static <T extends BaseEntity> Specification<T> projectEquals(String project) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(AbstractEntity_.PROJECT), project);
        };
    }

    public static <T extends BaseEntity> Specification<T> createdByEquals(String user) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(AbstractEntity_.CREATED_BY), user);
        };
    }

    public static <T extends BaseEntity> Specification<T> updatedByEquals(String user) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(AbstractEntity_.UPDATED_BY), user);
        };
    }

    public static <T extends BaseEntity> Specification<T> nameEquals(String name) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(Fields.NAME), name);
        };
    }

    public static <T extends BaseEntity> Specification<T> kindEquals(String kind) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(AbstractEntity_.KIND), kind);
        };
    }

    public static <T extends BaseEntity> Specification<T> latest() {
        return (root, query, criteriaBuilder) -> {
            Subquery<Number> subquery = query.subquery(Number.class);
            Root<T> subqueryRoot = (Root<T>) subquery.from(root.getJavaType());

            subquery.select(criteriaBuilder.max(subqueryRoot.get(AbstractEntity_.CREATED)));
            subquery.where(criteriaBuilder.equal(subqueryRoot.get(Fields.NAME), root.get(Fields.NAME)));
            subquery.groupBy(subqueryRoot.get(Fields.NAME), subqueryRoot.get(AbstractEntity_.PROJECT));

            return criteriaBuilder.and(criteriaBuilder.in(root.get(AbstractEntity_.CREATED)).value(subquery));
        };
    }

    public static <T extends BaseEntity> Specification<T> latestByProject(String project) {
        return (root, query, criteriaBuilder) -> {
            Subquery<Number> subquery = query.subquery(Number.class);
            Root<T> subqueryRoot = (Root<T>) subquery.from(root.getJavaType());

            subquery.select(criteriaBuilder.max(subqueryRoot.get(AbstractEntity_.CREATED)));
            subquery.where(
                criteriaBuilder.equal(subqueryRoot.get(AbstractEntity_.PROJECT), project),
                criteriaBuilder.equal(subqueryRoot.get(Fields.NAME), root.get(Fields.NAME))
            );
            subquery.groupBy(subqueryRoot.get(Fields.NAME), subqueryRoot.get(AbstractEntity_.PROJECT));

            return criteriaBuilder.and(
                criteriaBuilder.equal(root.get(AbstractEntity_.PROJECT), project),
                criteriaBuilder.in(root.get(AbstractEntity_.CREATED)).value(subquery)
            );
        };
    }

    public static <T extends BaseEntity> Specification<T> latestByProject(String project, String name) {
        return (root, query, criteriaBuilder) -> {
            Subquery<Number> subquery = query.subquery(Number.class);
            Root<T> subqueryRoot = (Root<T>) subquery.from(root.getJavaType());

            subquery.select(criteriaBuilder.max(subqueryRoot.get(AbstractEntity_.CREATED)));
            subquery.where(
                criteriaBuilder.equal(subqueryRoot.get(AbstractEntity_.PROJECT), project),
                criteriaBuilder.equal(subqueryRoot.get(Fields.NAME), name)
            );

            return criteriaBuilder.and(
                criteriaBuilder.equal(root.get(AbstractEntity_.PROJECT), project),
                criteriaBuilder.equal(root.get(Fields.NAME), name),
                criteriaBuilder.in(root.get(AbstractEntity_.CREATED)).value(subquery)
            );
        };
    }

    private CommonSpecification() {}
}
