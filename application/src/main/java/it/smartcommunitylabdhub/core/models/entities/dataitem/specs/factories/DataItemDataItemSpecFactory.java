package it.smartcommunitylabdhub.core.models.entities.dataitem.specs.factories;

import it.smartcommunitylabdhub.commons.infrastructure.factories.specs.SpecFactory;
import it.smartcommunitylabdhub.core.models.entities.dataitem.specs.DataItemDataItemSpec;
import org.springframework.stereotype.Component;

@Component
public class DataItemDataItemSpecFactory
  implements SpecFactory<DataItemDataItemSpec> {

  @Override
  public DataItemDataItemSpec create() {
    return new DataItemDataItemSpec();
  }
}
