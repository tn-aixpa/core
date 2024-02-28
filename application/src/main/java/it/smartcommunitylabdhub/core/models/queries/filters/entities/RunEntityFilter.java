package it.smartcommunitylabdhub.core.models.queries.filters.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.models.queries.SearchCriteria;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.core.models.base.BaseEntityFilter;
import it.smartcommunitylabdhub.core.models.base.BaseEntitySearchCriteria;
import it.smartcommunitylabdhub.core.models.entities.run.RunEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.abstracts.AbstractEntityFilter;
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
    @Pattern(regexp = Keys.PATH_PATTERN)
    @Schema(example = "kind://my-project/my-function:function-id", defaultValue = "", description = "Task path")
    private String task;

    @Override
    public SearchFilter<RunEntity> toSearchFilter() {
        List<SearchCriteria<RunEntity>> criteria = new ArrayList<>();

        //base criteria
        criteria.addAll(super.toSearchFilter().getCriteria());

        //task exact match
        Optional
            .ofNullable(task)
            .ifPresent(value ->
                criteria.add(new BaseEntitySearchCriteria<>("task", value, SearchCriteria.Operation.equal))
            );

        return BaseEntityFilter.<RunEntity>builder().criteria(criteria).condition(SearchFilter.Condition.and).build();
    }
}
