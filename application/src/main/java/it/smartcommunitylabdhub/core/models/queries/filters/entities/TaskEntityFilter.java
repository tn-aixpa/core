package it.smartcommunitylabdhub.core.models.queries.filters.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.Fields;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.models.queries.SearchCriteria;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.core.models.base.BaseEntityFilter;
import it.smartcommunitylabdhub.core.models.base.BaseEntitySearchCriteria;
import it.smartcommunitylabdhub.core.models.entities.TaskEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.abstracts.AbstractEntityFilter;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
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

    @Nullable
    @Pattern(regexp = Keys.FUNCTION_PATTERN)
    @Schema(example = "kind://my-project/my-function:function-id", defaultValue = "", description = "Function path")
    private String function;

    @Nullable
    @Pattern(regexp = Keys.WORKFLOW_PATTERN)
    @Schema(example = "kind://my-project/my-workflow:workflow-id", defaultValue = "", description = "Workflow path")
    private String workflow;

    @Override
    public SearchFilter<TaskEntity> toSearchFilter() {
        List<SearchCriteria<TaskEntity>> criteria = new ArrayList<>();

        //base criteria
        criteria.addAll(super.toSearchFilter().getCriteria());

        //function exact match
        Optional
            .ofNullable(function)
            .ifPresent(value ->
                criteria.add(new BaseEntitySearchCriteria<>(Fields.FUNCTION, value, SearchCriteria.Operation.equal))
            );

        //workflow exact match
        Optional
            .ofNullable(workflow)
            .ifPresent(value ->
                criteria.add(new BaseEntitySearchCriteria<>(Fields.WORKFLOW, value, SearchCriteria.Operation.equal))
            );

        return BaseEntityFilter.<TaskEntity>builder().criteria(criteria).condition(SearchFilter.Condition.and).build();
    }
}
