package it.smartcommunitylabdhub.commons.models.entities.project.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;

import it.smartcommunitylabdhub.commons.models.base.metadata.BaseMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectMetadata extends BaseMetadata {

  String name;

  String version;

  String description;

  Boolean embedded;
}
