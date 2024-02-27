package it.smartcommunitylabdhub.commons.models.base;

import it.smartcommunitylabdhub.commons.models.queries.SearchCriteria;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseEntityFilter<T extends BaseEntity> implements SearchFilter<T> {

    private List<SearchCriteria<T>> criteria;
    private Condition condition;

    @Override
    public Specification<T> toSpecification() {
        List<Specification<T>> specs = criteria.stream().map(f -> f.toSpecification()).collect(Collectors.toList());
        if (specs == null) {
            return null;
        }

        switch (condition) {
            case Condition.and:
                return Specification.allOf(specs);
            case Condition.or:
                return Specification.anyOf(specs);
            default:
                return null;
        }
    }
}
