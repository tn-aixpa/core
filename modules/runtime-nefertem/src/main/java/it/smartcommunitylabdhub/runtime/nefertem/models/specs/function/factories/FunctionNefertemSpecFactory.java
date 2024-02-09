package it.smartcommunitylabdhub.runtime.nefertem.models.specs.function.factories;

import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.runtime.nefertem.models.specs.function.FunctionNefertemSpec;
import org.springframework.stereotype.Component;

@Component
public class FunctionNefertemSpecFactory
  implements SpecFactory<FunctionNefertemSpec> {

  @Override
  public FunctionNefertemSpec create() {
    return new FunctionNefertemSpec();
  }
}
