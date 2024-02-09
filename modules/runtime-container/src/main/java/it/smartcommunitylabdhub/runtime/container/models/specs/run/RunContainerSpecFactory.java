package it.smartcommunitylabdhub.runtime.container.models.specs.run;

import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecFactory;
import org.springframework.stereotype.Component;

@Component
public class RunContainerSpecFactory implements SpecFactory<RunContainerSpec> {

  @Override
  public RunContainerSpec create() {
    return RunContainerSpec.builder().build();
  }
}
