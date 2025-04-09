package it.smartcommunitylabdhub.core.models.queries.filters.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.Keys;
import it.smartcommunitylabdhub.commons.models.queries.SearchCriteria;
import it.smartcommunitylabdhub.commons.models.queries.SearchFilter;
import it.smartcommunitylabdhub.core.models.base.BaseEntityFilter;
import it.smartcommunitylabdhub.core.models.base.BaseEntitySearchCriteria;
import it.smartcommunitylabdhub.core.models.entities.ReportEntity;
import it.smartcommunitylabdhub.core.models.queries.filters.abstracts.AbstractEntityFilter;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportEntityFilter extends AbstractEntityFilter<ReportEntity> {
    @Nullable
    @Pattern(regexp = Keys.SLUG_PATTERN)
    @Schema(example = "1231-asdf", defaultValue = "", description = "Entity id")
    private String entity;

    @Override
    public SearchFilter<ReportEntity> toSearchFilter() {
        List<SearchCriteria<ReportEntity>> criteria = new ArrayList<>();

        //base criteria
        criteria.addAll(super.toSearchFilter().getCriteria());

        //function exact match
        Optional
            .ofNullable(entity)
            .ifPresent(value ->
                criteria.add(new BaseEntitySearchCriteria<>("entity", value, SearchCriteria.Operation.equal))
            );

        return BaseEntityFilter.<ReportEntity>builder().criteria(criteria).condition(SearchFilter.Condition.and).build();
    }
}
