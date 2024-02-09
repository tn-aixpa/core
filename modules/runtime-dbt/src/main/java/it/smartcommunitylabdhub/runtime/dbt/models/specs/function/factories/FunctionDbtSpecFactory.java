package it.smartcommunitylabdhub.runtime.dbt.models.specs.function.factories;

import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.runtime.dbt.models.specs.function.FunctionDbtSpec;

import org.springframework.stereotype.Component;

@Component
public class FunctionDbtSpecFactory implements SpecFactory<FunctionDbtSpec> {

  @Override
  public FunctionDbtSpec create() {
    return new FunctionDbtSpec();
  }
}
