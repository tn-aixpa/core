package it.smartcommunitylabdhub.core.models.entities.log.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.core.models.base.metadata.BaseMetadata;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogMetadata extends BaseMetadata {
    String name;

    String run;

    String version;

    String description;

    Boolean embedded;
}