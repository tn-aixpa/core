package it.smartcommunitylabdhub.core.runs.filters;

import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.models.queries.SearchCriteria;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.core.queries.filters.AbstractEntityFilter;
import it.smartcommunitylabdhub.core.queries.filters.BaseEntityFilter;
import it.smartcommunitylabdhub.core.queries.filters.BaseEntitySearchCriteria;
import it.smartcommunitylabdhub.core.runs.persistence.RunEntity;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RunEntityFilter extends AbstractEntityFilter<RunEntity> {

    @Nullable
    @Pattern(regexp = Keys.TASK_PATTERN)
    @Schema(example = "kind://my-project/task-id", defaultValue = "", description = "Task path")
    private String task;

    @Override
    public SearchFilter<RunEntity> toSearchFilter() {
        List<SearchCriteria<RunEntity>> criteria = new ArrayList<>();

        //base criteria
        criteria.addAll(super.toSearchFilter().getCriteria());

        //if name replace with id like + task like
        criteria
            .stream()
            .filter(c -> c.getField().equals("name"))
            .findFirst()
            .ifPresent(c -> {
                criteria.remove(c);
                criteria.add(new BaseEntitySearchCriteria<>("id", c.getValue(), SearchCriteria.Operation.like));
            });

        //task exact match
        Optional
            .ofNullable(task)
            .ifPresent(value ->
                criteria.add(new BaseEntitySearchCriteria<>("task", value, SearchCriteria.Operation.equal))
            );

        return BaseEntityFilter.<RunEntity>builder().criteria(criteria).condition(SearchFilter.Condition.and).build();
    }
}
