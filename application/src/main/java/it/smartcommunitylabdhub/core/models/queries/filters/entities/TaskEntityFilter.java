package it.smartcommunitylabdhub.core.models.queries.filters.entities;

import it.smartcommunitylabdhub.commons.models.queries.SearchCriteria;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.core.models.base.BaseEntityFilter;
import it.smartcommunitylabdhub.core.models.base.BaseEntitySearchCriteria;
import it.smartcommunitylabdhub.core.models.entities.task.TaskEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.abstracts.AbstractEntityFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntityFilter extends AbstractEntityFilter<TaskEntity> {

    private String function;

    @Override
    public SearchFilter<TaskEntity> toSearchFilter() {
        List<SearchCriteria<TaskEntity>> criteria = new ArrayList<>();

        //base criteria
        criteria.addAll(super.toSearchFilter().getCriteria());

        //function exact match
        Optional
            .ofNullable(function)
            .ifPresent(value ->
                criteria.add(new BaseEntitySearchCriteria<>("function", value, SearchCriteria.Operation.equal))
            );

        return BaseEntityFilter.<TaskEntity>builder().criteria(criteria).condition(SearchFilter.Condition.and).build();
    }
}
