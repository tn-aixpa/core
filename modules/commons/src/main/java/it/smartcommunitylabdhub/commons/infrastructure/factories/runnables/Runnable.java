package it.smartcommunitylabdhub.commons.infrastructure.factories.runnables;

import java.io.Serializable;

public interface Runnable extends Serializable {
  String getFramework();

  String getProject();

  String getId();
}
