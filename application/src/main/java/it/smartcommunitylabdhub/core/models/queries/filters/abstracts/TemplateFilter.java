package it.smartcommunitylabdhub.core.models.queries.filters.abstracts;

import io.swagger.v3.oas.annotations.media.Schema;
import it.smartcommunitylabdhub.commons.Keys;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class TemplateFilter {
	@Nullable
    protected String q;

    @Nullable
    @Pattern(regexp = Keys.SLUG_PATTERN)
    @Schema(example = "my-function-1", defaultValue = "", description = "Name identifier")
    protected String name;

    @Nullable
    @Pattern(regexp = Keys.SLUG_PATTERN)
    @Schema(example = "function", defaultValue = "", description = "Kind identifier")
    protected String kind;
}
