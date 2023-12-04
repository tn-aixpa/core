package it.smartcommunitylabdhub.core.models.entities.log.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.smartcommunitylabdhub.core.models.base.metadata.BaseMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogMetadata extends BaseMetadata {
    String name;

    String run;

    String version;

    String description;

    Boolean embedded;
}