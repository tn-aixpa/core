package it.smartcommunitylabdhub.commons.models.queries;

import it.smartcommunitylabdhub.commons.models.base.BaseEntity;
import java.io.Serializable;
import org.springframework.data.jpa.domain.Specification;

public interface SearchCriteria<T extends BaseEntity> extends Specification<T> {
    String getField();
    Serializable getValue();
    Operation getOperation();

    Specification<T> toSpecification();

    public enum Operation {
        equal,
        gt,
        lt,
        like,
    }
}
