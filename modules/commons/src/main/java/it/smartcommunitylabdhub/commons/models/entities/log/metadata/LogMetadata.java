package it.smartcommunitylabdhub.commons.models.entities.log.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.commons.models.base.BaseMetadata;
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
