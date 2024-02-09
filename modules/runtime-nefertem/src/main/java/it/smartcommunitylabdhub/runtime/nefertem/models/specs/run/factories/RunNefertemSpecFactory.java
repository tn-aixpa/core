package it.smartcommunitylabdhub.runtime.nefertem.models.specs.run.factories;

import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.run.RunNefertemSpec;
import org.springframework.stereotype.Component;

@Component
public class RunNefertemSpecFactory implements SpecFactory<RunNefertemSpec> {

  @Override
  public RunNefertemSpec create() {
    return RunNefertemSpec.builder().build();
  }
}
