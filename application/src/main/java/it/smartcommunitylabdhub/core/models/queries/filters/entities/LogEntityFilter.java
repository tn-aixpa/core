package it.smartcommunitylabdhub.core.models.queries.filters.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.models.queries.SearchCriteria;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.core.models.entities.LogEntity;
import it.smartcommunitylabdhub.core.search.base.AbstractEntityFilter;
import it.smartcommunitylabdhub.core.search.base.BaseEntityFilter;
import it.smartcommunitylabdhub.core.search.base.BaseEntitySearchCriteria;
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
public class LogEntityFilter extends AbstractEntityFilter<LogEntity> {

    @Nullable
    @Pattern(regexp = Keys.SLUG_PATTERN)
    @Schema(example = "1231-asdf", defaultValue = "", description = "Run id")
    private String run;

    @Override
    public SearchFilter<LogEntity> toSearchFilter() {
        List<SearchCriteria<LogEntity>> criteria = new ArrayList<>();

        //base criteria
        criteria.addAll(super.toSearchFilter().getCriteria());

        //function exact match
        Optional
            .ofNullable(run)
            .ifPresent(value ->
                criteria.add(new BaseEntitySearchCriteria<>("run", value, SearchCriteria.Operation.equal))
            );

        return BaseEntityFilter.<LogEntity>builder().criteria(criteria).condition(SearchFilter.Condition.and).build();
    }
}
