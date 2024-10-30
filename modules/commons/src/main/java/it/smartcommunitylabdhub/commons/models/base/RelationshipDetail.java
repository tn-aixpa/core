package it.smartcommunitylabdhub.commons.models.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.enums.RelationshipName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelationshipDetail {

    @NotNull
    private RelationshipName type;

    @Nullable
    private String source;

    @Nullable
    private String dest;
}
