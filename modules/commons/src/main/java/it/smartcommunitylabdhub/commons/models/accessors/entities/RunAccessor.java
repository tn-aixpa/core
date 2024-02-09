package it.smartcommunitylabdhub.commons.models.accessors.entities;

import lombok.Getter;
import lombok.Setter;

//TODO remove, this is the same as the field the accessor
@Getter
@Setter
public class RunAccessor {

  private String runtime;
  private String task;
  private String project;
  private String name;
  private String version;

  public RunAccessor(
    String kind,
    String perform,
    String project,
    String function,
    String version
  ) {
    this.runtime = kind;
    this.task = perform;
    this.project = project;
    this.name = function;
    this.version = version;
  }
}
